// src/main/java/common/model/StudyLog.java
package main.java.common.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class StudyLog {
    private int id;
    private int subjectId;       // 과목 ID (선택사항, 0이면 과목 미지정)
    private String subjectName;  // 과목명 (직접 입력도 가능)
    private LocalDate studyDate; // 공부한 날짜
    private LocalTime startTime; // 시작 시간 (선택사항)
    private LocalTime endTime;   // 종료 시간 (선택사항)
    private int studyMinutes;    // 실제 공부한 분 수
    private String memo;         // 메모
    private String studyType;    // 공부 유형: "강의", "복습", "과제", "시험준비"
    private int rating;          // 집중도 평가 (1-5점)
    
    // 기본 생성자
    public StudyLog() {
        this.studyDate = LocalDate.now();
        this.studyType = "복습";
        this.rating = 3;
    }
    
    // 기본 매개변수 생성자 (과목명 + 공부시간)
    public StudyLog(String subjectName, LocalDate studyDate, int studyMinutes) {
        this();
        this.subjectName = subjectName;
        this.studyDate = studyDate;
        this.studyMinutes = studyMinutes;
    }
    
    // 시간 포함 생성자
    public StudyLog(String subjectName, LocalDate studyDate, 
                   LocalTime startTime, LocalTime endTime) {
        this();
        this.subjectName = subjectName;
        this.studyDate = studyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        if (startTime != null && endTime != null) {
            this.studyMinutes = calculateMinutes(startTime, endTime);
        }
    }
    
    // 전체 매개변수 생성자
    public StudyLog(int id, int subjectId, String subjectName, LocalDate studyDate,
                   LocalTime startTime, LocalTime endTime, int studyMinutes, 
                   String memo, String studyType, int rating) {
        this.id = id;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.studyDate = studyDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.studyMinutes = studyMinutes;
        this.memo = memo;
        this.studyType = studyType;
        this.rating = rating;
    }
    
    // 시간 차이 계산 (분 단위)
    private int calculateMinutes(LocalTime start, LocalTime end) {
        if (start == null || end == null) return 0;
        return (int) java.time.Duration.between(start, end).toMinutes();
    }
    
    // Getters
    public int getId() { return id; }
    public int getSubjectId() { return subjectId; }
    public String getSubjectName() { return subjectName; }
    public LocalDate getStudyDate() { return studyDate; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getStudyMinutes() { return studyMinutes; }
    public String getMemo() { return memo; }
    public String getStudyType() { return studyType; }
    public int getRating() { return rating; }
    
    // Setters
    public void setId(int id) { this.id = id; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public void setStudyDate(LocalDate studyDate) { this.studyDate = studyDate; }
    
    public void setStartTime(LocalTime startTime) { 
        this.startTime = startTime;
        updateStudyMinutes();
    }
    
    public void setEndTime(LocalTime endTime) { 
        this.endTime = endTime;
        updateStudyMinutes();
    }
    
    public void setStudyMinutes(int studyMinutes) { this.studyMinutes = studyMinutes; }
    public void setMemo(String memo) { this.memo = memo; }
    public void setStudyType(String studyType) { this.studyType = studyType; }
    
    public void setRating(int rating) { 
        if (rating >= 1 && rating <= 5) {
            this.rating = rating;
        }
    }
    
    // 시작/종료 시간이 변경될 때 자동으로 분 수 계산
    private void updateStudyMinutes() {
        if (startTime != null && endTime != null) {
            this.studyMinutes = calculateMinutes(startTime, endTime);
        }
    }
    
    // 유틸리티 메서드들
    
    // 공부 시간을 시간:분 형태로 반환
    public String getStudyTimeFormatted() {
        int hours = studyMinutes / 60;
        int mins = studyMinutes % 60;
        if (hours > 0) {
            return String.format("%d시간 %d분", hours, mins);
        } else {
            return String.format("%d분", mins);
        }
    }
    
    // 집중도를 텍스트로 반환
    public String getRatingText() {
        switch (rating) {
            case 1: return "매우 나쁨";
            case 2: return "나쁨";
            case 3: return "보통";
            case 4: return "좋음";
            case 5: return "매우 좋음";
            default: return "미평가";
        }
    }
    
    // 집중도를 이모지로 반환
    public String getRatingEmoji() {
        switch (rating) {
            case 1: return "😫";
            case 2: return "😕";
            case 3: return "😐";
            case 4: return "😊";
            case 5: return "😄";
            default: return "❓";
        }
    }
    
    // 공부 유형에 따른 이모지
    public String getStudyTypeEmoji() {
        switch (studyType) {
            case "강의": return "👨‍🏫";
            case "복습": return "📖";
            case "과제": return "✏️";
            case "시험준비": return "📚";
            default: return "📝";
        }
    }
    
    // 효율적인 공부였는지 판단 (1시간 이상 + 집중도 4점 이상)
    public boolean isEffectiveStudy() {
        return studyMinutes >= 60 && rating >= 4;
    }
    
    // 오늘 공부한 것인지 확인
    public boolean isToday() {
        return studyDate.equals(LocalDate.now());
    }
    
    // 이번 주 공부한 것인지 확인
    public boolean isThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);
        
        return !studyDate.isBefore(weekStart) && !studyDate.isAfter(weekEnd);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s (%s) %s %s", 
                           studyDate, 
                           subjectName != null ? subjectName : "미지정", 
                           getStudyTimeFormatted(),
                           getStudyTypeEmoji(),
                           getRatingEmoji());
    }
    
    // 상세 정보 문자열
    public String toDetailString() {
        StringBuilder sb = new StringBuilder();
        sb.append("📅 날짜: ").append(studyDate).append("\n");
        sb.append("📚 과목: ").append(subjectName != null ? subjectName : "미지정").append("\n");
        sb.append("⏰ 시간: ");
        if (startTime != null && endTime != null) {
            sb.append(startTime).append(" ~ ").append(endTime).append(" ");
        }
        sb.append("(").append(getStudyTimeFormatted()).append(")\n");
        sb.append("📝 유형: ").append(getStudyTypeEmoji()).append(" ").append(studyType).append("\n");
        sb.append("⭐ 집중도: ").append(getRatingEmoji()).append(" ").append(getRatingText()).append("\n");
        if (memo != null && !memo.trim().isEmpty()) {
            sb.append("💭 메모: ").append(memo);
        }
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudyLog studyLog = (StudyLog) o;
        return id == studyLog.id;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}