package models;

public class ResultRecord {
    private final int examId;
    private final int studentId;
    private final int marksObtained;
    private final String filePath;
    private final String unitName;

    public ResultRecord(int examId, int studentId, int marksObtained, String filePath, String unitName) {
        this.examId = examId;
        this.studentId = studentId;
        this.marksObtained = marksObtained;
        this.filePath = filePath;
        this.unitName = unitName;
    }

    public int getExamId() { return examId; }
    public int getStudentId() { return studentId; }
    public int getMarksObtained() { return marksObtained; }
    public String getFilePath() { return filePath; }
    public String getUnitName() { return unitName; }
}
