// src/main/java/common/model/Exam.java
package main.java.common.model;

import java.time.LocalDateTime;

public class Exam {
    private int id;
    private int subjectId;           // 과목 ID
    private String title;            // 시험명
    private String type;             // 시험 유형: "중간고사", "기말고사", "쪽지시험"
    private LocalDateTime examDateTime; // 시험 일시
    private String location;         // 시험 장소
    private String description;      // 시험 설명/범위
    
    // 기본 생성자
    public Exam() {}
    
    // 기본 매개변수 생성자
    public Exam(int subjectId, String title, String type, LocalDateTime examDateTime) {
        this.subjectId = subjectId;
        this.title = title;
        this.type = type;
        this.examDateTime = examDateTime;
    }
    
    // 전체 매개변수 생성자
    public Exam(int id, int subjectId, String title, String type, 
               LocalDateTime examDateTime, String location, String description) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.type = type;
        this.examDateTime = examDateTime;
        this.location = location;
        this.description = description;
    }
    
    // Getters
    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public LocalDateTime getExamDateTime() { return examDateTime; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setExamDateTime(LocalDateTime examDateTime) { this.examDateTime = examDateTime; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return title + " (" + type + ", " + examDateTime.toLocalDate() + ")";
    }
    
    // 시험까지 남은 시간 계산 (시간 단위)
    public long getHoursLeft() {
        return java.time.temporal.ChronoUnit.HOURS.between(LocalDateTime.now(), examDateTime);
    }
    
    // 시험이 임박했는지 확인 (24시간 이내)
    public boolean isImminent() {
        return getHoursLeft() <= 24 && getHoursLeft() > 0;
    }
}