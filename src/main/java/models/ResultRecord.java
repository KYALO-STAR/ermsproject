package models;

public class ResultRecord {
    private final int examId;
    private final int studentId;
    private final int marksObtained;
    private final String filePath;
    private final String unitName;
    private final String studentName; // New field

    public ResultRecord(int examId, int studentId, int marksObtained, String filePath, String unitName, String studentName) {
        this.examId = examId;
        this.studentId = studentId;
        this.marksObtained = marksObtained;
        this.filePath = filePath;
        this.unitName = unitName;
        this.studentName = studentName;
    }

    public int getExamId() { return examId; }
    public int getStudentId() { return studentId; }
    public int getMarksObtained() { return marksObtained; }
    public String getFilePath() { return filePath; }
    public String getUnitName() { return unitName; }
    public String getStudentName() { return studentName; } // New getter
}
