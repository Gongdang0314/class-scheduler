// src/main/java/common/model/Subject.java
package common.model;

public class Subject {
    private int id;
    private String name;         // 과목명
    private int credits;         // 학점
    private String professor;    // 교수명
    private String classroom;    // 강의실
    private String category;     // 전공필수/전공선택/교양
    private String dayOfWeek;    // 요일
    private String startTime;    // 시작시간
    private String endTime;      // 종료시간
    
    // 기본 생성자
    public Subject() {}
    
    // 기본 매개변수 생성자
    public Subject(String name, int credits, String professor) {
        this.name = name;
        this.credits = credits;
        this.professor = professor;
    }
    
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getCredits() { return credits; }
    public String getProfessor() { return professor; }
    public String getClassroom() { return classroom; }
    public String getCategory() { return category; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCredits(int credits) { this.credits = credits; }
    public void setProfessor(String professor) { this.professor = professor; }
    public void setClassroom(String classroom) { this.classroom = classroom; }
    public void setCategory(String category) { this.category = category; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    
    @Override
    public String toString() {
        return name + " (" + credits + "학점, " + professor + ")";
    }
}