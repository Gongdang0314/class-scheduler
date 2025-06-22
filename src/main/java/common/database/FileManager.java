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
import common.model.Grade;
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
                if (subject != null) subjects.add(subject);
            }
            System.out.println("📂 과목 데이터 로드 완료: " + subjects.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 과목 파일이 없음 - 빈 리스트 반환");
        }
        return subjects;
    }

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
            for (Assignment asg : assignments) {
                writer.println(assignmentToString(asg));
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
                Assignment asg = stringToAssignment(line);
                if (asg != null) assignments.add(asg);
            }
            System.out.println("📂 과제 데이터 로드 완료: " + assignments.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 과제 파일이 없음 - 빈 리스트 반환");
        }
        return assignments;
    }

    private String assignmentToString(Assignment a) {
        return a.getId() + "|" +
               a.getSubjectId() + "|" +
               nullToEmpty(a.getTitle()) + "|" +
               nullToEmpty(a.getDescription()) + "|" +
               (a.getDueDate() != null ? a.getDueDate().toString() : "") + "|" +
               nullToEmpty(a.getStatus()) + "|" +
               nullToEmpty(a.getPriority());
    }

    private Assignment stringToAssignment(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                Assignment asg = new Assignment();
                asg.setId(Integer.parseInt(parts[0]));
                asg.setSubjectId(Integer.parseInt(parts[1]));
                asg.setTitle(emptyToNull(parts[2]));
                asg.setDescription(emptyToNull(parts[3]));
                if (!parts[4].isEmpty()) asg.setDueDate(LocalDate.parse(parts[4]));
                asg.setStatus(emptyToNull(parts[5]));
                asg.setPriority(emptyToNull(parts[6]));
                return asg;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 과제 데이터 파싱 오류: " + line);
        }
        return null;
    }

    // === EXAM 저장/로드 ===
    public void saveExams(List<Exam> exams) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "exams.txt"))) {
            for (Exam ex : exams) {
                writer.println(examToString(ex));
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
                Exam ex = stringToExam(line);
                if (ex != null) exams.add(ex);
            }
            System.out.println("📂 시험 데이터 로드 완료: " + exams.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 시험 파일이 없음 - 빈 리스트 반환");
        }
        return exams;
    }

    private String examToString(Exam e) {
        return e.getId() + "|" +
               e.getSubjectId() + "|" +
               nullToEmpty(e.getTitle()) + "|" +
               nullToEmpty(e.getType()) + "|" +
               (e.getExamDateTime() != null ? e.getExamDateTime().toString() : "") + "|" +
               nullToEmpty(e.getLocation()) + "|" +
               nullToEmpty(e.getDescription());
    }

    private Exam stringToExam(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                Exam ex = new Exam();
                ex.setId(Integer.parseInt(parts[0]));
                ex.setSubjectId(Integer.parseInt(parts[1]));
                ex.setTitle(emptyToNull(parts[2]));
                ex.setType(emptyToNull(parts[3]));
                if (!parts[4].isEmpty()) ex.setExamDateTime(LocalDateTime.parse(parts[4]));
                ex.setLocation(emptyToNull(parts[5]));
                ex.setDescription(emptyToNull(parts[6]));
                return ex;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 시험 데이터 파싱 오류: " + line);
        }
        return null;
    }

    // === GradeRecord 저장/로드 ===
    public void saveGrades(List<GradeRecord> grades) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "grades.txt"))) {
            for (GradeRecord gr : grades) {
                writer.println(gradeToString(gr));
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
                GradeRecord gr = stringToGrade(line);
                if (gr != null) grades.add(gr);
            }
            System.out.println("📂 성적 데이터 로드 완료: " + grades.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 성적 파일이 없음 - 빈 리스트 반환");
        }
        return grades;
    }

    private String gradeToString(GradeRecord grade) {
        return grade.getId() + "|" +
               grade.getSubjectId() + "|" +
               nullToEmpty(grade.getSemester()) + "|" +
               grade.getScore() + "|" +
               nullToEmpty(grade.getLetterGrade()) + "|" +
               grade.getGradePoint();
    }

    private GradeRecord stringToGrade(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                GradeRecord gr = new GradeRecord();
                gr.setId(Integer.parseInt(parts[0]));
                gr.setSubjectId(Integer.parseInt(parts[1]));
                gr.setSemester(emptyToNull(parts[2]));
                gr.setScore(Double.parseDouble(parts[3]));
                gr.setLetterGrade(emptyToNull(parts[4]));
                gr.setGradePoint(Double.parseDouble(parts[5]));
                return gr;
            }
        } catch (Exception e) {
            System.err.println("⚠️ 성적 데이터 파싱 오류: " + line);
        }
        return null;
    }

    // === 사용자 Grade 저장/로드 (UI용 Grade 기반) ===
    private String userGradeToString(Grade grade) {
        return grade.getSubjectName() + "|" +
               grade.getLetterGrade()  + "|" +
               grade.getGpa()          + "|" +
               grade.getCredit()       + "|" +
               grade.isMajor();
    }

    private Grade stringToUserGrade(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                String subj   = parts[0];
                String let    = parts[1];
                double gpa    = Double.parseDouble(parts[2]);
                int credit    = Integer.parseInt(parts[3]);
                boolean major = Boolean.parseBoolean(parts[4]);
                return new Grade(subj, let, gpa, credit, major);
            }
        } catch (Exception e) {
            System.err.println("⚠️ 사용자 성적 파싱 오류: " + line);
        }
        return null;
    }

    public void saveUserGrades(List<Grade> grades) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + "user_grades.txt"))) {
            for (Grade g : grades) {
                writer.println(userGradeToString(g));
            }
            System.out.println("💾 사용자 성적 저장 완료: " + grades.size() + "개");
        } catch (IOException e) {
            System.err.println("❌ 사용자 성적 저장 실패: " + e.getMessage());
        }
    }

    public List<Grade> loadUserGrades() {
        List<Grade> grades = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DATA_DIR + "user_grades.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Grade g = stringToUserGrade(line);
                if (g != null) grades.add(g);
            }
            System.out.println("📂 사용자 성적 로드 완료: " + grades.size() + "개");
        } catch (IOException e) {
            System.out.println("📄 사용자 성적 파일이 없음 - 빈 리스트 반환");
        }
        return grades;
    }

    // === 유틸리티 메서드 ===
    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    private String emptyToNull(String str) {
        return str.isEmpty() ? null : str;
    }

    public boolean fileExists(String fileName) {
        return new File(DATA_DIR + fileName).exists();
    }

    public boolean deleteFile(String fileName) {
        return new File(DATA_DIR + fileName).delete();
    }

    public void createBackup(String fileName) {
        try {
            File orig = new File(DATA_DIR + fileName);
            if (orig.exists()) {
                String backup = "backup_" + System.currentTimeMillis() + "_" + fileName;
                try (BufferedReader r = new BufferedReader(new FileReader(orig));
                     PrintWriter w = new PrintWriter(new FileWriter(DATA_DIR + backup))) {
                    String line;
                    while ((line = r.readLine()) != null) w.println(line);
                }
                System.out.println("🔄 백업 생성: " + backup);
            }
        } catch (IOException e) {
            System.err.println("❌ 백업 실패: " + e.getMessage());
        }
    }
}
