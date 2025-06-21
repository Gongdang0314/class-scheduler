// src/main/java/common/utils/GradeUtils.java
package main.java.common.utils;

import main.java.common.model.GradeRecord;
import main.java.common.model.Subject;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GradeUtils {
    
    // 전체 평점 계산 (학점 가중평균)
    public static double calculateGPA(List<GradeRecord> grades, List<Subject> subjects) {
        double totalPoints = 0.0;
        int totalCredits = 0;
        
        // 과목 ID로 빠른 검색을 위한 Map 생성
        Map<Integer, Subject> subjectMap = new HashMap<>();
        for (Subject subject : subjects) {
            subjectMap.put(subject.getId(), subject);
        }
        
        for (GradeRecord grade : grades) {
            Subject subject = subjectMap.get(grade.getSubjectId());
            if (subject != null && !grade.getLetterGrade().equals("F")) {
                totalPoints += grade.getGradePoint() * subject.getCredits();
                totalCredits += subject.getCredits();
            }
        }
        
        return totalCredits > 0 ? totalPoints / totalCredits : 0.0;
    }
    
    // 학기별 평점 계산
    public static double calculateSemesterGPA(String semester, List<GradeRecord> grades, List<Subject> subjects) {
        List<GradeRecord> semesterGrades = grades.stream()
                .filter(grade -> grade.getSemester().equals(semester))
                .toList();
        
        return calculateGPA(semesterGrades, subjects);
    }
    
    // 카테고리별 이수학점 계산
    public static Map<String, Integer> calculateCreditsByCategory(List<GradeRecord> grades, List<Subject> subjects) {
        Map<String, Integer> categoryCredits = new HashMap<>();
        categoryCredits.put("전공필수", 0);
        categoryCredits.put("전공선택", 0);
        categoryCredits.put("교양", 0);
        categoryCredits.put("자유선택", 0);
        
        Map<Integer, Subject> subjectMap = new HashMap<>();
        for (Subject subject : subjects) {
            subjectMap.put(subject.getId(), subject);
        }
        
        for (GradeRecord grade : grades) {
            // F학점이 아닌 경우만 이수학점으로 인정
            if (!grade.getLetterGrade().equals("F")) {
                Subject subject = subjectMap.get(grade.getSubjectId());
                if (subject != null && subject.getCategory() != null) {
                    String category = subject.getCategory();
                    if (categoryCredits.containsKey(category)) {
                        categoryCredits.put(category, categoryCredits.get(category) + subject.getCredits());
                    }
                }
            }
        }
        
        return categoryCredits;
    }
    
    // 점수를 등급으로 변환
    public static String convertScoreToLetterGrade(double score) {
        if (score >= 95) return "A+";
        else if (score >= 90) return "A";
        else if (score >= 85) return "B+";
        else if (score >= 80) return "B";
        else if (score >= 75) return "C+";
        else if (score >= 70) return "C";
        else if (score >= 65) return "D+";
        else if (score >= 60) return "D";
        else return "F";
    }
    
    // 등급을 평점으로 변환
    public static double convertLetterGradeToPoint(String letterGrade) {
        switch (letterGrade) {
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
    
    // 총 이수학점 계산
    public static int calculateTotalCredits(List<GradeRecord> grades, List<Subject> subjects) {
        Map<String, Integer> categoryCredits = calculateCreditsByCategory(grades, subjects);
        return categoryCredits.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    // 졸업 요건 체크 (예시)
    public static boolean checkGraduationRequirements(List<GradeRecord> grades, List<Subject> subjects) {
        Map<String, Integer> categoryCredits = calculateCreditsByCategory(grades, subjects);
        
        // 예시 졸업 요건 (실제로는 설정에서 관리 가능)
        return categoryCredits.get("전공필수") >= 60 &&    // 전공필수 60학점
               categoryCredits.get("전공선택") >= 30 &&    // 전공선택 30학점
               categoryCredits.get("교양") >= 30 &&        // 교양 30학점
               calculateTotalCredits(grades, subjects) >= 130; // 총 130학점
    }
    
    // 성적 분포 계산 (A+, A, B+ 등의 개수)
    public static Map<String, Integer> calculateGradeDistribution(List<GradeRecord> grades) {
        Map<String, Integer> distribution = new HashMap<>();
        String[] gradeTypes = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        
        // 초기화
        for (String grade : gradeTypes) {
            distribution.put(grade, 0);
        }
        
        // 계산
        for (GradeRecord grade : grades) {
            String letterGrade = grade.getLetterGrade();
            distribution.put(letterGrade, distribution.get(letterGrade) + 1);
        }
        
        return distribution;
    }
    
    // 평점대별 학점 수 계산
    public static Map<String, Integer> calculateGradeRangeCredits(List<GradeRecord> grades, List<Subject> subjects) {
        Map<String, Integer> rangeCredits = new HashMap<>();
        rangeCredits.put("4.0 이상", 0);    // A+, A
        rangeCredits.put("3.0-3.9", 0);    // B+, B
        rangeCredits.put("2.0-2.9", 0);    // C+, C
        rangeCredits.put("1.0-1.9", 0);    // D+, D
        rangeCredits.put("F", 0);          // F
        
        Map<Integer, Subject> subjectMap = new HashMap<>();
        for (Subject subject : subjects) {
            subjectMap.put(subject.getId(), subject);
        }
        
        for (GradeRecord grade : grades) {
            Subject subject = subjectMap.get(grade.getSubjectId());
            if (subject != null) {
                double point = grade.getGradePoint();
                String range;
                
                if (point >= 4.0) range = "4.0 이상";
                else if (point >= 3.0) range = "3.0-3.9";
                else if (point >= 2.0) range = "2.0-2.9";
                else if (point >= 1.0) range = "1.0-1.9";
                else range = "F";
                
                rangeCredits.put(range, rangeCredits.get(range) + subject.getCredits());
            }
        }
        
        return rangeCredits;
    }
    
    // 평점 등급 반환 (예: "우수", "보통", "미흡")
    public static String getGPAGrade(double gpa) {
        if (gpa >= 4.0) return "최우수";
        else if (gpa >= 3.5) return "우수";
        else if (gpa >= 3.0) return "보통";
        else if (gpa >= 2.5) return "미흡";
        else return "경고";
    }
}