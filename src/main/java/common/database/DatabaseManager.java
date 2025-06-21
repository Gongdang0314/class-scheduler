// src/main/java/common/database/DatabaseManager.java
package main.java.common.database;

import main.java.common.model.*;
import main.java.common.utils.DateUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class DatabaseManager {
    private static DatabaseManager instance;
    private FileManager fileManager;
    
    // 메모리 캐시 (빠른 접근을 위해)
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Exam> exams;
    private List<GradeRecord> grades;
    
    // 싱글톤 패턴
    private DatabaseManager() {
        fileManager = new FileManager();
        loadAllData();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    // 모든 데이터 로드
    private void loadAllData() {
        subjects = fileManager.loadSubjects();
        assignments = fileManager.loadAssignments();
        exams = fileManager.loadExams();
        grades = fileManager.loadGrades();
        
        System.out.println("🔄 모든 데이터 로드 완료");
    }
    
    // 모든 데이터 저장
    public void saveAllData() {
        fileManager.saveSubjects(subjects);
        fileManager.saveAssignments(assignments);
        fileManager.saveExams(exams);
        fileManager.saveGrades(grades);
        
        System.out.println("💾 모든 데이터 저장 완료");
    }
    
    // ===== SUBJECT 관련 메서드 =====
    
    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }
    
    public Optional<Subject> getSubjectById(int id) {
        return subjects.stream()
                .filter(subject -> subject.getId() == id)
                .findFirst();
    }
    
    public List<Subject> getSubjectsByDay(String dayOfWeek) {
        return subjects.stream()
                .filter(subject -> dayOfWeek.equals(subject.getDayOfWeek()))
                .toList();
    }
    
    public void addSubject(Subject subject) {
        subject.setId(generateNewSubjectId());
        subjects.add(subject);
        fileManager.saveSubjects(subjects);
        System.out.println("➕ 과목 추가: " + subject.getName());
    }
    
    public boolean updateSubject(Subject updatedSubject) {
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getId() == updatedSubject.getId()) {
                subjects.set(i, updatedSubject);
                fileManager.saveSubjects(subjects);
                System.out.println("✏️ 과목 수정: " + updatedSubject.getName());
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteSubject(int id) {
        Optional<Subject> subjectToDelete = getSubjectById(id);
        
        boolean removed = subjects.removeIf(subject -> subject.getId() == id);
        if (removed) {
            // 관련 데이터도 삭제
            assignments.removeIf(assignment -> assignment.getSubjectId() == id);
            exams.removeIf(exam -> exam.getSubjectId() == id);
            grades.removeIf(grade -> grade.getSubjectId() == id);
            
            saveAllData();
            System.out.println("🗑️ 과목 삭제 완료 (관련 데이터 포함): " + 
                             (subjectToDelete.isPresent() ? subjectToDelete.get().getName() : "ID " + id));
        }
        return removed;
    }
    
    // ===== ASSIGNMENT 관련 메서드 =====
    
    public List<Assignment> getAllAssignments() {
        return new ArrayList<>(assignments);
    }
    
    public List<Assignment> getAssignmentsBySubject(int subjectId) {
        return assignments.stream()
                .filter(assignment -> assignment.getSubjectId() == subjectId)
                .toList();
    }
    
    public List<Assignment> getAssignmentsByStatus(String status) {
        return assignments.stream()
                .filter(assignment -> status.equals(assignment.getStatus()))
                .toList();
    }
    
    public List<Assignment> getUrgentAssignments() {
        return assignments.stream()
                .filter(Assignment::isUrgent)
                .toList();
    }
    
    public Optional<Assignment> getAssignmentById(int id) {
        return assignments.stream()
                .filter(assignment -> assignment.getId() == id)
                .findFirst();
    }
    
    public void addAssignment(Assignment assignment) {
        assignment.setId(generateNewAssignmentId());
        assignments.add(assignment);
        fileManager.saveAssignments(assignments);
        System.out.println("➕ 과제 추가: " + assignment.getTitle());
    }
    
    public boolean updateAssignment(Assignment updatedAssignment) {
        for (int i = 0; i < assignments.size(); i++) {
            if (assignments.get(i).getId() == updatedAssignment.getId()) {
                assignments.set(i, updatedAssignment);
                fileManager.saveAssignments(assignments);
                System.out.println("✏️ 과제 수정: " + updatedAssignment.getTitle());
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteAssignment(int id) {
        Optional<Assignment> assignmentToDelete = getAssignmentById(id);
        
        boolean removed = assignments.removeIf(assignment -> assignment.getId() == id);
        if (removed) {
            fileManager.saveAssignments(assignments);
            System.out.println("🗑️ 과제 삭제: " + 
                             (assignmentToDelete.isPresent() ? assignmentToDelete.get().getTitle() : "ID " + id));
        }
        return removed;
    }
    
    // ===== EXAM 관련 메서드 =====
    
    public List<Exam> getAllExams() {
        return new ArrayList<>(exams);
    }
    
    public List<Exam> getExamsBySubject(int subjectId) {
        return exams.stream()
                .filter(exam -> exam.getSubjectId() == subjectId)
                .toList();
    }
    
    public List<Exam> getExamsByType(String type) {
        return exams.stream()
                .filter(exam -> type.equals(exam.getType()))
                .toList();
    }
    
    public List<Exam> getImminentExams() {
        return exams.stream()
                .filter(Exam::isImminent)
                .toList();
    }
    
    public Optional<Exam> getExamById(int id) {
        return exams.stream()
                .filter(exam -> exam.getId() == id)
                .findFirst();
    }
    
    public void addExam(Exam exam) {
        exam.setId(generateNewExamId());
        exams.add(exam);
        fileManager.saveExams(exams);
        System.out.println("➕ 시험 추가: " + exam.getTitle());
    }
    
    public boolean updateExam(Exam updatedExam) {
        for (int i = 0; i < exams.size(); i++) {
            if (exams.get(i).getId() == updatedExam.getId()) {
                exams.set(i, updatedExam);
                fileManager.saveExams(exams);
                System.out.println("✏️ 시험 수정: " + updatedExam.getTitle());
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteExam(int id) {
        Optional<Exam> examToDelete = getExamById(id);
        
        boolean removed = exams.removeIf(exam -> exam.getId() == id);
        if (removed) {
            fileManager.saveExams(exams);
            System.out.println("🗑️ 시험 삭제: " + 
                             (examToDelete.isPresent() ? examToDelete.get().getTitle() : "ID " + id));
        }
        return removed;
    }
    
    // ===== GRADE 관련 메서드 =====
    
    public List<GradeRecord> getAllGrades() {
        return new ArrayList<>(grades);
    }
    
    public List<GradeRecord> getGradesBySemester(String semester) {
        return grades.stream()
                .filter(grade -> semester.equals(grade.getSemester()))
                .toList();
    }
    
    public List<GradeRecord> getCurrentSemesterGrades() {
        String currentSemester = DateUtils.getCurrentSemester();
        return getGradesBySemester(currentSemester);
    }
    
    public Optional<GradeRecord> getGradeById(int id) {
        return grades.stream()
                .filter(grade -> grade.getId() == id)
                .findFirst();
    }
    
    public Optional<GradeRecord> getGradeBySubjectAndSemester(int subjectId, String semester) {
        return grades.stream()
                .filter(grade -> grade.getSubjectId() == subjectId && 
                               semester.equals(grade.getSemester()))
                .findFirst();
    }
    
    public void addGrade(GradeRecord grade) {
        grade.setId(generateNewGradeId());
        grades.add(grade);
        fileManager.saveGrades(grades);
        System.out.println("➕ 성적 추가: " + grade.getLetterGrade());
    }
    
    public boolean updateGrade(GradeRecord updatedGrade) {
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i).getId() == updatedGrade.getId()) {
                grades.set(i, updatedGrade);
                fileManager.saveGrades(grades);
                System.out.println("✏️ 성적 수정: " + updatedGrade.getLetterGrade());
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteGrade(int id) {
        boolean removed = grades.removeIf(grade -> grade.getId() == id);
        if (removed) {
            fileManager.saveGrades(grades);
            System.out.println("🗑️ 성적 삭제: ID " + id);
        }
        return removed;
    }
    
    // ===== ID 생성 메서드들 =====
    
    private int generateNewSubjectId() {
        return subjects.stream()
                .mapToInt(Subject::getId)
                .max()
                .orElse(0) + 1;
    }
    
    private int generateNewAssignmentId() {
        return assignments.stream()
                .mapToInt(Assignment::getId)
                .max()
                .orElse(0) + 1;
    }
    
    private int generateNewExamId() {
        return exams.stream()
                .mapToInt(Exam::getId)
                .max()
                .orElse(0) + 1;
    }
    
    private int generateNewGradeId() {
        return grades.stream()
                .mapToInt(GradeRecord::getId)
                .max()
                .orElse(0) + 1;
    }
    
    // ===== 통계 및 유틸리티 메서드 =====
    
    // 데이터베이스 상태 정보
    public String getDatabaseStatus() {
        return String.format(
            "📊 데이터베이스 현황\n" +
            "과목: %d개\n" +
            "과제: %d개 (급한 과제: %d개)\n" +
            "시험: %d개 (임박한 시험: %d개)\n" +
            "성적: %d개",
            subjects.size(),
            assignments.size(),
            getUrgentAssignments().size(),
            exams.size(),
            getImminentExams().size(),
            grades.size()
        );
    }
    
    // 과목별 과제/시험 개수 통계
    public void printSubjectStatistics() {
        System.out.println("\n📈 과목별 통계:");
        for (Subject subject : subjects) {
            int assignmentCount = getAssignmentsBySubject(subject.getId()).size();
            int examCount = getExamsBySubject(subject.getId()).size();
            System.out.printf("  %s: 과제 %d개, 시험 %d개\n", 
                            subject.getName(), assignmentCount, examCount);
        }
    }
    
    // 백업 생성
    public void createBackup() {
        fileManager.createBackup("subjects.txt");
        fileManager.createBackup("assignments.txt");
        fileManager.createBackup("exams.txt");
        fileManager.createBackup("grades.txt");
        System.out.println("🔄 전체 백업 완료");
    }
    
    // 데이터 초기화 (개발/테스트용)
    public void clearAllData() {
        subjects.clear();
        assignments.clear();
        exams.clear();
        grades.clear();
        saveAllData();
        System.out.println("🧹 모든 데이터 초기화 완료");
    }
    
    // 데이터 다시 로드
    public void reloadData() {
        loadAllData();
        System.out.println("🔄 데이터 다시 로드 완료");
    }
}