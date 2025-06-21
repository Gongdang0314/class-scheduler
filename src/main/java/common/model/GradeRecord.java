// src/main/java/common/model/GradeRecord.java
package common.model;

public class GradeRecord {
    private int id;
    private int subjectId;       // 과목 ID
    private String semester;     // 학기 (예: "2024-1", "2024-2")
    private double score;        // 점수 (0-100)
    private String letterGrade;  // 등급 (A+, A, B+, B, C+, C, D+, D, F)
    private double gradePoint;   // 평점 (4.5 만점)
    
    // 기본 생성자
    public GradeRecord() {}
    
    // 기본 매개변수 생성자
    public GradeRecord(int subjectId, String semester, double score, String letterGrade) {
        this.subjectId = subjectId;
        this.semester = semester;
        this.score = score;
        this.letterGrade = letterGrade;
        this.gradePoint = convertLetterToPoint(letterGrade);
    }
    
    // 전체 매개변수 생성자
    public GradeRecord(int id, int subjectId, String semester, double score, 
                      String letterGrade, double gradePoint) {
        this.id = id;
        this.subjectId = subjectId;
        this.semester = semester;
        this.score = score;
        this.letterGrade = letterGrade;
        this.gradePoint = gradePoint;
    }
    
    // 등급을 평점으로 변환하는 메서드
    private double convertLetterToPoint(String letter) {
        switch (letter) {
            case "A+": return 4.5;
            case "A": return 4.0;
            case "B+": return 3.5;
            case "B": return 3.0;
            case "C+": return 2.5;
            case "C": return 2.0;
            case "D+": return 1.5;
            case "D": return 1.0;
            case "F": return 0.0;
            default: return 0.0;
        }
    }
    
    // Getters
    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getSemester() { return semester; }
    public double getScore() { return score; }
    public String getLetterGrade() { return letterGrade; }
    public double getGradePoint() { return gradePoint; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setScore(double score) { this.score = score; }
    public void setLetterGrade(String letterGrade) { 
        this.letterGrade = letterGrade;
        this.gradePoint = convertLetterToPoint(letterGrade); // 자동 계산
    }
    public void setGradePoint(double gradePoint) { this.gradePoint = gradePoint; }
    
    @Override
    public String toString() {
        return semester + " - " + letterGrade + " (" + score + "점, " + gradePoint + "평점)";
    }
    
    // 점수로부터 등급 자동 계산
    public void calculateGradeFromScore() {
        if (score >= 95) this.letterGrade = "A+";
        else if (score >= 90) this.letterGrade = "A";
        else if (score >= 85) this.letterGrade = "B+";
        else if (score >= 80) this.letterGrade = "B";
        else if (score >= 75) this.letterGrade = "C+";
        else if (score >= 70) this.letterGrade = "C";
        else if (score >= 65) this.letterGrade = "D+";
        else if (score >= 60) this.letterGrade = "D";
        else this.letterGrade = "F";
        
        this.gradePoint = convertLetterToPoint(this.letterGrade);
    }
    
    // 이수 여부 확인
    public boolean isPassed() {
        return !letterGrade.equals("F");
    }
}