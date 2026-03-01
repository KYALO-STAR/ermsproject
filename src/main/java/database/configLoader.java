package database;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class configLoader {
    private static Properties props = new Properties();

    static {
        // List of possible locations where config.properties might be
        String[] paths = {
            "config.properties",      // Root folder (next to src)
            "src/config.properties",  // Inside src folder
            "bin/config.properties"   // Inside compiled folder
        };

        boolean loaded = false;
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                    System.out.println("✅ Config loaded successfully from: " + file.getAbsolutePath());
                    loaded = true;
                    break; 
                } catch (Exception e) {
                    System.err.println("❌ Error reading file at " + path);
                }
            }
        }

        if (!loaded) {
            System.err.println(" CRITICAL ERROR: config.properties not found!");
            System.err.println("Working Directory: " + System.getProperty("user.dir"));
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}