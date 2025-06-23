package common.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import common.listeners.DataChangeListener;
import common.model.Assignment;
import common.model.Exam;
import common.model.Grade;
import common.model.GradeRecord;
import common.model.Subject;
import common.utils.DateUtils;

public class DatabaseManager {
    private static DatabaseManager instance;
    private FileManager fileManager;
    
    // Observer 패턴을 위한 리스너 목록 (thread-safe)
    private final List<DataChangeListener> listeners = new CopyOnWriteArrayList<>();
    
    // 메모리 캐시 (빠른 접근을 위해)
    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Exam> exams;
    private List<GradeRecord> grades;
    private List<Grade> userGrades;
    
    // 싱글톤 패턴
    private DatabaseManager() {
        fileManager = new FileManager();
        loadAllData();
        userGrades = fileManager.loadUserGrades();
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * 데이터 변경 리스너 등록
     */
    public void addDataChangeListener(DataChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            System.out.println("🔗 데이터 변경 리스너 등록: " + listener.getClass().getSimpleName());
        }
    }
    
    /**
     * 데이터 변경 리스너 제거
     */
    public void removeDataChangeListener(DataChangeListener listener) {
        if (listener != null) {
            listeners.remove(listener);
            System.out.println("🔗 데이터 변경 리스너 제거: " + listener.getClass().getSimpleName());
        }
    }
    
    /**
     * 과목 변경 알림
     */
    private void notifySubjectChanged(String changeType, int subjectId) {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onSubjectChanged(changeType, subjectId);
            } catch (Exception e) {
                System.err.println("❌ 리스너 알림 중 오류: " + e.getMessage());
            }
        }
    }
    
    /**
     * 과제 변경 알림
     */
    private void notifyAssignmentChanged(String changeType, int assignmentId) {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onAssignmentChanged(changeType, assignmentId);
            } catch (Exception e) {
                System.err.println("❌ 리스너 알림 중 오류: " + e.getMessage());
            }
        }
    }
    
    /**
     * 시험 변경 알림
     */
    private void notifyExamChanged(String changeType, int examId) {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onExamChanged(changeType, examId);
            } catch (Exception e) {
                System.err.println("❌ 리스너 알림 중 오류: " + e.getMessage());
            }
        }
    }
    
    /**
     * 성적 변경 알림
     */
    private void notifyGradeChanged(String changeType, int gradeId) {
        for (DataChangeListener listener : listeners) {
            try {
                listener.onGradeChanged(changeType, gradeId);
            } catch (Exception e) {
                System.err.println("❌ 리스너 알림 중 오류: " + e.getMessage());
            }
        }
    }
    
    // 모든 데이터 로드
    private void loadAllData() {
        subjects    = fileManager.loadSubjects();
        assignments = fileManager.loadAssignments();
        exams       = fileManager.loadExams();
        grades      = fileManager.loadGrades();
        
        System.out.println("🔄 모든 데이터 로드 완료");
    }
    
    // 모든 데이터 저장
    public void saveAllData() {
        fileManager.saveSubjects(subjects);
        fileManager.saveAssignments(assignments);
        fileManager.saveExams(exams);
        fileManager.saveGrades(grades);
        fileManager.saveUserGrades(userGrades);
        
        System.out.println("💾 모든 데이터 저장 완료");
    }
    
    // ===== SUBJECT 관련 메서드 =====
    
    /**
     * 모든 과목을 가져옵니다.
     * 같은 이름(name)이 중복된 과목이 있으면 하나만 남기고 제거합니다.
     */
    public List<Subject> getAllSubjects() {
        // name → Subject 맵으로 수집하면서 첫 번째 등장만 유지
        Map<String, Subject> unique = subjects.stream()
            .collect(Collectors.toMap(
                Subject::getName,
                s -> s,
                (existing, replacement) -> existing
            ));
        return new ArrayList<>(unique.values());
    }
    
    public Optional<Subject> getSubjectById(int id) {
        return subjects.stream()
                .filter(subject -> subject.getId() == id)
                .findFirst();
    }
    
    /**
     * 요일별 과목 조회도 중복 제거된 리스트에서 수행합니다.
     */
    public List<Subject> getSubjectsByDay(String dayOfWeek) {
        return getAllSubjects().stream()
                .filter(subject -> dayOfWeek.equals(subject.getDayOfWeek()))
                .collect(Collectors.toList());
    }
    
    public void addSubject(Subject subject) {
        subject.setId(generateNewSubjectId());
        subjects.add(subject);
        fileManager.saveSubjects(subjects);
        System.out.println("➕ 과목 추가: " + subject.getName());
        
        // 리스너들에게 알림
        notifySubjectChanged("ADD", subject.getId());
    }
    
    public boolean updateSubject(Subject updatedSubject) {
        for (int i = 0; i < subjects.size(); i++) {
            if (subjects.get(i).getId() == updatedSubject.getId()) {
                subjects.set(i, updatedSubject);
                fileManager.saveSubjects(subjects);
                System.out.println("✏️ 과목 수정: " + updatedSubject.getName());
                
                // 리스너들에게 알림
                notifySubjectChanged("UPDATE", updatedSubject.getId());
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
            
            // 리스너들에게 알림
            notifySubjectChanged("DELETE", id);
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
                .collect(Collectors.toList());
    }
    
    public List<Assignment> getAssignmentsByStatus(String status) {
        return assignments.stream()
                .filter(assignment -> status.equals(assignment.getStatus()))
                .collect(Collectors.toList());
    }
    
    public List<Assignment> getUrgentAssignments() {
        return assignments.stream()
                .filter(Assignment::isUrgent)
                .collect(Collectors.toList());
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
        
        // 리스너들에게 알림
        notifyAssignmentChanged("ADD", assignment.getId());
    }
    
    public boolean updateAssignment(Assignment updatedAssignment) {
        for (int i = 0; i < assignments.size(); i++) {
            if (assignments.get(i).getId() == updatedAssignment.getId()) {
                assignments.set(i, updatedAssignment);
                fileManager.saveAssignments(assignments);
                System.out.println("✏️ 과제 수정: " + updatedAssignment.getTitle());
                
                // 리스너들에게 알림
                notifyAssignmentChanged("UPDATE", updatedAssignment.getId());
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
            
            // 리스너들에게 알림
            notifyAssignmentChanged("DELETE", id);
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
                .collect(Collectors.toList());
    }
    
    public List<Exam> getExamsByType(String type) {
        return exams.stream()
                .filter(exam -> type.equals(exam.getType()))
                .collect(Collectors.toList());
    }
    
    public List<Exam> getImminentExams() {
        return exams.stream()
                .filter(Exam::isImminent)
                .collect(Collectors.toList());
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
        
        // 리스너들에게 알림
        notifyExamChanged("ADD", exam.getId());
    }
    
    public boolean updateExam(Exam updatedExam) {
        for (int i = 0; i < exams.size(); i++) {
            if (exams.get(i).getId() == updatedExam.getId()) {
                exams.set(i, updatedExam);
                fileManager.saveExams(exams);
                System.out.println("✏️ 시험 수정: " + updatedExam.getTitle());
                
                // 리스너들에게 알림
                notifyExamChanged("UPDATE", updatedExam.getId());
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
            
            // 리스너들에게 알림
            notifyExamChanged("DELETE", id);
        }
        return removed;
    }

    // ===== GRADE RECORD 관련 메서드 =====

    public List<GradeRecord> getAllGrades() {
        return new ArrayList<>(grades);
    }

    public List<GradeRecord> getGradesBySemester(String semester) {
        return grades.stream()
                .filter(grade -> semester.equals(grade.getSemester()))
                .collect(Collectors.toList());
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
        
        // 리스너들에게 알림
        notifyGradeChanged("ADD", grade.getId());
    }

    public boolean updateGrade(GradeRecord updatedGrade) {
        for (int i = 0; i < grades.size(); i++) {
            if (grades.get(i).getId() == updatedGrade.getId()) {
                grades.set(i, updatedGrade);
                fileManager.saveGrades(grades);
                System.out.println("✏️ 성적 수정: " + updatedGrade.getLetterGrade());
                
                // 리스너들에게 알림
                notifyGradeChanged("UPDATE", updatedGrade.getId());
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
            
            // 리스너들에게 알림
            notifyGradeChanged("DELETE", id);
        }
        return removed;
    }

    /** UI에서 저장한 사용자 성적 불러오기 */
    public List<Grade> getUserGrades() {
        // 파일에서 최신으로 불러와 캐시에 덮어쓰기
        userGrades = fileManager.loadUserGrades();
        return new ArrayList<>(userGrades);
    }

    /** UI에서 전달된 사용자 성적 저장 */
    public void saveUserGrades(List<Grade> grades) {
        this.userGrades = new ArrayList<>(grades);
        fileManager.saveUserGrades(grades);
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

    /** 데이터베이스 상태 정보 */
    public String getDatabaseStatus() {
        return String.format(
            "📊 데이터베이스 현황\n" +
            "과목: %d개\n" +
            "과제: %d개 (급한 과제: %d개)\n" +
            "시험: %d개 (임박한 시험: %d개)\n" +
            "성적: %d개\n" +
            "등록된 리스너: %d개",
            subjects.size(),
            assignments.size(),
            getUrgentAssignments().size(),
            exams.size(),
            getImminentExams().size(),
            grades.size(),
            listeners.size()
        );
    }

    /** 백업 생성 */
    public void createBackup() {
        fileManager.createBackup("subjects.txt");
        fileManager.createBackup("assignments.txt");
        fileManager.createBackup("exams.txt");
        fileManager.createBackup("grades.txt");
        System.out.println("🔄 전체 백업 완료");
    }

    /** 데이터 초기화 (개발/테스트용) */
    public void clearAllData() {
        subjects.clear();
        assignments.clear();
        exams.clear();
        grades.clear();
        saveAllData();
        
        // 모든 리스너에게 삭제 알림 (ID -1은 전체 삭제를 의미)
        notifySubjectChanged("CLEAR", -1);
        
        System.out.println("🧹 모든 데이터 초기화 완료");
    }

    /** 데이터 다시 로드 */
    public void reloadData() {
        loadAllData();
        userGrades = fileManager.loadUserGrades();
        
        // 모든 리스너에게 새로고침 알림
        notifySubjectChanged("RELOAD", -1);
        
        System.out.println("🔄 데이터 다시 로드 완료");
    }
}