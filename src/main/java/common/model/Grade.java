package common.model;

public class Grade {

    private String subjectName;
    private String letterGrade;
    private double gpa;
    private int credit;
    private boolean isMajor;

    public Grade(String subjectName, String letterGrade, double gpa, int credit, boolean isMajor) {
        this.subjectName = subjectName;
        this.letterGrade = letterGrade;
        this.gpa = gpa;
        this.credit = credit;
        this.isMajor = isMajor;
    }

    // Getter들
    public String getSubjectName() {
        return subjectName;
    }

    public String getLetterGrade() {
        return letterGrade;
    }

    public double getGpa() {
        return gpa;
    }

    public int getCredit() {
        return credit;
    }

    public boolean isMajor() {
        return isMajor;
    }

    // Optional: toString()
    @Override
    public String toString() {
        return subjectName + " (" + letterGrade + ", " + gpa + ", " + credit + "학점, " + (isMajor ? "전공" : "교양") + ")";
    }
}
