// src/main/java/common/model/Assignment.java
package main.java.common.model;

import java.time.LocalDate;

public class Assignment {
    private int id;
    private int subjectId;       // 과목 ID (외래키)
    private String title;        // 과제 제목
    private String description;  // 과제 설명
    private LocalDate dueDate;   // 마감일
    private String status;       // 진행상태: "미완료", "진행중", "완료"
    private String priority;     // 우선순위: "낮음", "보통", "높음"
    
    // 기본 생성자
    public Assignment() {
        this.status = "미완료";
        this.priority = "보통";
    }
    
    // 기본 매개변수 생성자
    public Assignment(int subjectId, String title, LocalDate dueDate) {
        this();
        this.subjectId = subjectId;
        this.title = title;
        this.dueDate = dueDate;
    }
    
    // 전체 매개변수 생성자
    public Assignment(int id, int subjectId, String title, String description, 
                     LocalDate dueDate, String status, String priority) {
        this.id = id;
        this.subjectId = subjectId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }
    
    // Getters
    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
    
    @Override
    public String toString() {
        return title + " (마감: " + dueDate + ", 상태: " + status + ")";
    }
    
    // 마감일까지 남은 일수 계산 (유틸리티 메서드)
    public long getDaysLeft() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
    
    // 과제가 급한지 확인
    public boolean isUrgent() {
        return getDaysLeft() <= 2;
    }
}