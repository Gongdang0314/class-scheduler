// src/main/java/common/utils/DateUtils.java
package main.java.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    // D-day 계산
    public static long calculateDDay(LocalDate targetDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
    }
    
    // D-day 문자열 반환
    public static String getDDayString(LocalDate targetDate) {
        long days = calculateDDay(targetDate);
        
        if (days == 0) {
            return "D-Day";
        } else if (days > 0) {
            return "D-" + days;
        } else {
            return "D+" + Math.abs(days);
        }
    }
    
    // 현재 학기 반환 (예: "2024-1")
    public static String getCurrentSemester() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        // 3월~8월: 1학기, 9월~2월: 2학기
        if (month >= 3 && month <= 8) {
            return year + "-1";
        } else {
            if (month >= 9) {
                return year + "-2";
            } else {
                return (year - 1) + "-2";
            }
        }
    }
    
    // 요일을 한글로 변환
    public static String getDayOfWeekKorean(LocalDate date) {
        switch (date.getDayOfWeek()) {
            case MONDAY: return "월";
            case TUESDAY: return "화";
            case WEDNESDAY: return "수";
            case THURSDAY: return "목";
            case FRIDAY: return "금";
            case SATURDAY: return "토";
            case SUNDAY: return "일";
            default: return "";
        }
    }
    
    // 분을 시간:분 형식으로 변환
    public static String formatMinutesToHourMinute(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%d시간 %d분", hours, minutes);
    }
    
    // 문자열을 LocalDate로 변환
    public static LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMAT);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
    
    // 문자열을 LocalDateTime으로 변환
    public static LocalDateTime parseDateTime(String dateTimeString) {
        try {
            return LocalDateTime.parse(dateTimeString, DATETIME_FORMAT);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    
    // 오늘이 주말인지 확인
    public static boolean isWeekend() {
        LocalDate today = LocalDate.now();
        return today.getDayOfWeek().getValue() >= 6; // 토요일(6), 일요일(7)
    }
    
    // 이번 주 월요일 날짜
    public static LocalDate getThisWeekMonday() {
        LocalDate today = LocalDate.now();
        return today.minusDays(today.getDayOfWeek().getValue() - 1);
    }
    
    // 학기 시작일 추정 (3월 1일 또는 9월 1일)
    public static LocalDate getSemesterStartDate() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        if (month >= 3 && month <= 8) {
            return LocalDate.of(year, 3, 1); // 1학기
        } else {
            if (month >= 9) {
                return LocalDate.of(year, 9, 1); // 2학기
            } else {
                return LocalDate.of(year - 1, 9, 1); // 작년 2학기
            }
        }
    }
}