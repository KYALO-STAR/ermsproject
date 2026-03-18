package ui.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import ui.UploadProcessor;

/**
 * Minimal embedded HTTP server to accept PDF uploads from phone browsers.
 * Keeps the implementation intentionally small and dependency-free.
 */
public class UploaderServer {
    private HttpServer server;
    private int port;
    private int examId;

    public UploaderServer(int port, int examId) throws IOException {
        this.port = port;
        this.examId = examId;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/upload", new UploadHandler());
        server.setExecutor(null);
    }

    public void start() {
        server.start();
        System.out.println("UploaderServer started on port " + port);
    }

    public void stop() {
        server.stop(0);
        System.out.println("UploaderServer stopped");
    }

    class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                byte[] resp = getFormPage().getBytes("UTF-8");
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, resp.length);
                try (OutputStream os = exchange.getResponseBody()) { os.write(resp); }
                return;
            }

            if ("POST".equalsIgnoreCase(method)) {
                Headers headers = exchange.getRequestHeaders();
                String contentType = headers.getFirst("Content-Type");
                if (contentType == null || !contentType.contains("multipart/form-data")) {
                    sendText(exchange, 400, "Expected multipart/form-data");
                    return;
                }

                // Read entire request body into memory. This is simple and
                // acceptable for small PDFs used in the class project, but in
                // production you should stream and enforce a size limit.
                InputStream is = exchange.getRequestBody();
                byte[] body = readAllBytes(is);

                String boundary = getBoundary(contentType);
                if (boundary == null) {
                    sendText(exchange, 400, "Boundary not found");
                    return;
                }

                List<Part> parts = parseParts(body, boundary);
                int success = 0;

                // Find examId field override if present
                for (Part p : parts) {
                    if ("examId".equals(p.name) && p.value != null) {
                        try { examId = Integer.parseInt(p.value.trim()); } catch (Exception ignored) {}
                    }
                }

                // Save incoming files to a temporary folder. These files are
                // handed to UploadProcessor which will persist metadata to the DB.
                File uploadDir = new File(System.getProperty("java.io.tmpdir"), "erms_uploads");
                uploadDir.mkdirs();

                for (Part p : parts) {
                    if (p.filename != null && p.data != null) {
                        File out = new File(uploadDir, p.filename);
                        // Write the raw uploaded bytes to disk and then call the
                        // shared UploadProcessor to parse the filename and save
                        // the result to the database.
                        Files.write(out.toPath(), p.data);
                        System.out.println("Received file: " + out.getAbsolutePath());

                        if (UploadProcessor.processPdf(out, examId)) success++;
                    }
                }

                sendText(exchange, 200, "OK - processed " + success + " file(s)");
                return;
            }

            sendText(exchange, 405, "Method not allowed");
        }

        private String getFormPage() {
            return "<html><body>" +
                    "<h3>ERMS - Upload PDF (StudentID_Marks.pdf)</h3>" +
                    "<form method=post enctype=multipart/form-data>" +
                    "Exam ID: <input type=text name=examId /><br/><br/>" +
                    "Select PDF: <input type=file name=file accept='.pdf' /><br/><br/>" +
                    "<input type=submit value='Upload'/>" +
                    "</form></body></html>";
        }

        private void sendText(HttpExchange ex, int code, String txt) throws IOException {
            byte[] b = txt.getBytes("UTF-8");
            ex.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            ex.sendResponseHeaders(code, b.length);
            try (OutputStream os = ex.getResponseBody()) { os.write(b); }
        }

        private byte[] readAllBytes(InputStream is) throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int r;
            while ((r = is.read(buf)) != -1) out.write(buf, 0, r);
            return out.toByteArray();
        }

        private String getBoundary(String contentType) {
            String[] parts = contentType.split(";");
            for (String p : parts) {
                p = p.trim();
                if (p.startsWith("boundary=")) return p.substring("boundary=".length());
            }
            return null;
        }

        private List<Part> parseParts(byte[] body, String boundary) throws IOException {
            List<Part> parts = new ArrayList<>();
            String raw = new String(body, "ISO-8859-1");
            String[] split = raw.split("--" + boundary);
            for (String s : split) {
                if (s.equals("") || s.equals("--") || s.equals("--\r\n")) continue;
                int idx = s.indexOf("\r\n\r\n");
                if (idx < 0) continue;
                String hdr = s.substring(0, idx);
                String dataStr = s.substring(idx + 4);
                // remove trailing CRLF if present
                if (dataStr.endsWith("\r\n")) dataStr = dataStr.substring(0, dataStr.length() - 2);

                String name = null, filename = null;
                for (String line : hdr.split("\r\n")) {
                    line = line.trim();
                    if (line.toLowerCase().startsWith("content-disposition:")) {
                        // e.g. form-data; name="file"; filename="foo.pdf"
                        String[] tokens = line.split(";");
                        for (String t : tokens) {
                            t = t.trim();
                            if (t.startsWith("name=")) name = stripQuotes(t.substring(5));
                            if (t.startsWith("filename=")) filename = stripQuotes(t.substring(9));
                        }
                    }
                }

                // Build a Part object; binary file content is kept as ISO-8859-1
                // bytes which preserves the raw upload content.
                Part p = new Part();
                p.name = name;
                p.filename = filename;
                p.value = (filename == null) ? dataStr : null;
                if (filename != null) p.data = dataStr.getBytes("ISO-8859-1");
                parts.add(p);
            }
            return parts;
        }

        private String stripQuotes(String s) {
            s = s.trim();
            if (s.startsWith("\"") && s.endsWith("\"")) return s.substring(1, s.length() - 1);
            return s;
        }

        class Part { String name; String filename; String value; byte[] data; }
    }
}
