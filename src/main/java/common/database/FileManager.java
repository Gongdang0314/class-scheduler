// src/main/java/common/database/FileManager.java
package common.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import common.model.Assignment;
import common.model.Exam;
import common.model.GradeRecord;
import common.model.Subject;

public class FileManager {
    private static final String DATA_DIR = "data/";
    
    public FileManager() {
        createDataDirectory();
    }
    
    // 데이터 디렉토리 생성
    private void createDataDirectory() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("📁 데이터 디렉토리 생성: " + DATA_DIR);
            }
        }
    }
    
    // === SUBJECT 저장/로드 ===
    
    public void saveSubjects(List<Subject> subjects) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "subjects.txt"))) {
            for (Subject subject : subjects) {
                writer.println(subjectToString(subject));
            }
            System.out.println("💾 과목 데이터 저장 완료: " + subjects.size() + "개");
        } catch (IOException e) {
            System.err.println("❌ 과목 저장 실패: " + e.getMessage());
        }
    }
    
    public List<Subject> loadSubjects() {
        List<Subject> subjects = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "subjects.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Subject subject = stringToSubject(line);
                if (subject != null) {
                    subjects.add(subject);
                }
            }
            System.out.println("📂 과목 데이터 로드 완료: " + subjects.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 과목 파일이 없음 - 빈 리스트 반환");
        }
        return subjects;
    }
    
    // Subject를 문자열로 변환
    private String subjectToString(Subject subject) {
        return subject.getId() + "|" +
               nullToEmpty(subject.getName()) + "|" +
               subject.getCredits() + "|" +
               nullToEmpty(subject.getProfessor()) + "|" +
               nullToEmpty(subject.getClassroom()) + "|" +
               nullToEmpty(subject.getCategory()) + "|" +
               nullToEmpty(subject.getDayOfWeek()) + "|" +
               nullToEmpty(subject.getStartTime()) + "|" +
               nullToEmpty(subject.getEndTime());
    }
    
    // 문자열을 Subject로 변환
    private Subject stringToSubject(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 9) {
                Subject subject = new Subject();
                subject.setId(Integer.parseInt(parts[0]));
                subject.setName(emptyToNull(parts[1]));
                subject.setCredits(Integer.parseInt(parts[2]));
                subject.setProfessor(emptyToNull(parts[3]));
                subject.setClassroom(emptyToNull(parts[4]));
                subject.setCategory(emptyToNull(parts[5]));
                subject.setDayOfWeek(emptyToNull(parts[6]));
                subject.setStartTime(emptyToNull(parts[7]));
                subject.setEndTime(emptyToNull(parts[8]));
                return subject;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 과목 데이터 파싱 오류: " + line);
        }
        return null;
    }
    
    // === ASSIGNMENT 저장/로드 ===
    
    public void saveAssignments(List<Assignment> assignments) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "assignments.txt"))) {
            for (Assignment assignment : assignments) {
                writer.println(assignmentToString(assignment));
            }
            System.out.println("💾 과제 데이터 저장 완료: " + assignments.size() + "개");
        } catch (IOException e) {
            System.err.println("❌ 과제 저장 실패: " + e.getMessage());
        }
    }
    
    public List<Assignment> loadAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "assignments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Assignment assignment = stringToAssignment(line);
                if (assignment != null) {
                    assignments.add(assignment);
                }
            }
            System.out.println("📂 과제 데이터 로드 완료: " + assignments.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 과제 파일이 없음 - 빈 리스트 반환");
        }
        return assignments;
    }
    
    // Assignment를 문자열로 변환
    private String assignmentToString(Assignment assignment) {
        return assignment.getId() + "|" +
               assignment.getSubjectId() + "|" +
               nullToEmpty(assignment.getTitle()) + "|" +
               nullToEmpty(assignment.getDescription()) + "|" +
               (assignment.getDueDate() != null ? assignment.getDueDate().toString() : "") + "|" +
               nullToEmpty(assignment.getStatus()) + "|" +
               nullToEmpty(assignment.getPriority());
    }
    
    // 문자열을 Assignment로 변환
    private Assignment stringToAssignment(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                Assignment assignment = new Assignment();
                assignment.setId(Integer.parseInt(parts[0]));
                assignment.setSubjectId(Integer.parseInt(parts[1]));
                assignment.setTitle(emptyToNull(parts[2]));
                assignment.setDescription(emptyToNull(parts[3]));
                if (!parts[4].isEmpty()) {
                    assignment.setDueDate(LocalDate.parse(parts[4]));
                }
                assignment.setStatus(emptyToNull(parts[5]));
                assignment.setPriority(emptyToNull(parts[6]));
                return assignment;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 과제 데이터 파싱 오류: " + line);
        }
        return null;
    }
    
    // === EXAM 저장/로드 ===
    
    public void saveExams(List<Exam> exams) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "exams.txt"))) {
            for (Exam exam : exams) {
                writer.println(examToString(exam));
            }
            System.out.println("💾 시험 데이터 저장 완료: " + exams.size() + "개");
        } catch (IOException e) {
            System.err.println("❌ 시험 저장 실패: " + e.getMessage());
        }
    }
    
    public List<Exam> loadExams() {
        List<Exam> exams = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "exams.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Exam exam = stringToExam(line);
                if (exam != null) {
                    exams.add(exam);
                }
            }
            System.out.println("📂 시험 데이터 로드 완료: " + exams.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 시험 파일이 없음 - 빈 리스트 반환");
        }
        return exams;
    }
    
    // Exam을 문자열로 변환
    private String examToString(Exam exam) {
        return exam.getId() + "|" +
               exam.getSubjectId() + "|" +
               nullToEmpty(exam.getTitle()) + "|" +
               nullToEmpty(exam.getType()) + "|" +
               (exam.getExamDateTime() != null ? exam.getExamDateTime().toString() : "") + "|" +
               nullToEmpty(exam.getLocation()) + "|" +
               nullToEmpty(exam.getDescription());
    }
    
    // 문자열을 Exam으로 변환
    private Exam stringToExam(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                Exam exam = new Exam();
                exam.setId(Integer.parseInt(parts[0]));
                exam.setSubjectId(Integer.parseInt(parts[1]));
                exam.setTitle(emptyToNull(parts[2]));
                exam.setType(emptyToNull(parts[3]));
                if (!parts[4].isEmpty()) {
                    exam.setExamDateTime(LocalDateTime.parse(parts[4]));
                }
                exam.setLocation(emptyToNull(parts[5]));
                exam.setDescription(emptyToNull(parts[6]));
                return exam;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 시험 데이터 파싱 오류: " + line);
        }
        return null;
    }
    
    // === GRADE 저장/로드 ===
    
    public void saveGrades(List<GradeRecord> grades) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "grades.txt"))) {
            for (GradeRecord grade : grades) {
                writer.println(gradeToString(grade));
            }
            System.out.println("💾 성적 데이터 저장 완료: " + grades.size() + "개");
        } catch (IOException e) {
            System.err.println("❌ 성적 저장 실패: " + e.getMessage());
        }
    }
    
    public List<GradeRecord> loadGrades() {
        List<GradeRecord> grades = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "grades.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                GradeRecord grade = stringToGrade(line);
                if (grade != null) {
                    grades.add(grade);
                }
            }
            System.out.println("📂 성적 데이터 로드 완료: " + grades.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 성적 파일이 없음 - 빈 리스트 반환");
        }
        return grades;
    }
    
    // GradeRecord를 문자열로 변환
    private String gradeToString(GradeRecord grade) {
        return grade.getId() + "|" +
               grade.getSubjectId() + "|" +
               nullToEmpty(grade.getSemester()) + "|" +
               grade.getScore() + "|" +
               nullToEmpty(grade.getLetterGrade()) + "|" +
               grade.getGradePoint();
    }
    
    // 문자열을 GradeRecord로 변환
    private GradeRecord stringToGrade(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                GradeRecord grade = new GradeRecord();
                grade.setId(Integer.parseInt(parts[0]));
                grade.setSubjectId(Integer.parseInt(parts[1]));
                grade.setSemester(emptyToNull(parts[2]));
                grade.setScore(Double.parseDouble(parts[3]));
                grade.setLetterGrade(emptyToNull(parts[4]));
                grade.setGradePoint(Double.parseDouble(parts[5]));
                return grade;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 성적 데이터 파싱 오류: " + line);
        }
        return null;
    }
    
    // === 유틸리티 메서드 ===
    
    // null을 빈 문자열로 변환
    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }
    
    // 빈 문자열을 null로 변환
    private String emptyToNull(String str) {
        return str.isEmpty() ? null : str;
    }
    
    // 파일 존재 여부 확인
    public boolean fileExists(String fileName) {
        File file = new File(DATA_DIR + fileName);
        return file.exists();
    }
    
    // 파일 삭제
    public boolean deleteFile(String fileName) {
        File file = new File(DATA_DIR + fileName);
        return file.delete();
    }
    
    // 백업 생성
    public void createBackup(String fileName) {
        try {
            File originalFile = new File(DATA_DIR + fileName);
            if (originalFile.exists()) {
                String backupFileName = "backup_" + System.currentTimeMillis() + "_" + fileName;
                File backupFile = new File(DATA_DIR + backupFileName);
                
                try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
                     PrintWriter writer = new PrintWriter(new FileWriter(backupFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
                System.out.println("🔄 백업 생성 완료: " + backupFileName);
            }
        } catch (IOException e) {
            System.err.println("❌ 백업 생성 실패: " + e.getMessage());
        }
    }
}