// src/main/java/common/database/DatabaseManager.java
package common.database;

import java.util.ArrayList;
import java.util.List;

import common.model.Assignment;
import common.model.Exam;
import common.model.Grade;
import common.model.GradeRecord;
import common.model.Subject;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final FileManager fileManager;

    private List<Subject> subjects;
    private List<Assignment> assignments;
    private List<Exam> exams;
    private List<GradeRecord> gradeRecords;
    private List<Grade> userGrades;

    private DatabaseManager() {
        this.fileManager    = new FileManager();
        this.subjects       = fileManager.loadSubjects();
        this.assignments    = fileManager.loadAssignments();
        this.exams          = fileManager.loadExams();
        this.gradeRecords   = fileManager.loadGrades();
        this.userGrades     = fileManager.loadUserGrades();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) instance = new DatabaseManager();
        return instance;
    }

    // --- 기본 엔티티용 CRUD (필요시 추가/수정/삭제 메서드 구현) ---

    public List<Subject> getAllSubjects() {
        return new ArrayList<>(subjects);
    }
    public void saveSubjects(List<Subject> subs) {
        this.subjects = new ArrayList<>(subs);
        fileManager.saveSubjects(subs);
    }

    public List<Assignment> getAllAssignments() {
        return new ArrayList<>(assignments);
    }
    public void saveAssignments(List<Assignment> asgs) {
        this.assignments = new ArrayList<>(asgs);
        fileManager.saveAssignments(asgs);
    }

    public List<Exam> getAllExams() {
        return new ArrayList<>(exams);
    }
    public void saveExams(List<Exam> exs) {
        this.exams = new ArrayList<>(exs);
        fileManager.saveExams(exs);
    }

    public List<GradeRecord> getAllGradeRecords() {
        return new ArrayList<>(gradeRecords);
    }
    public void saveGradeRecords(List<GradeRecord> grs) {
        this.gradeRecords = new ArrayList<>(grs);
        fileManager.saveGrades(grs);
    }

    // --- UI용 Grade CRUD (GradeCalculatorPanel과 연동) ---

    /** UI에서 저장한 사용자 성적 리스트를 반환 */
    public List<Grade> getUserGrades() {
        this.userGrades = fileManager.loadUserGrades();
        return new ArrayList<>(userGrades);
    }

    /** UI에서 전달된 사용자 성적을 저장 */
    public void saveUserGrades(List<Grade> grades) {
        this.userGrades = new ArrayList<>(grades);
        fileManager.saveUserGrades(grades);
    }

    // --- 전체 데이터 관리 유틸 ---

    /** 메모리와 파일의 모든 데이터를 다시 로드 */
    public void reloadAll() {
        this.subjects     = fileManager.loadSubjects();
        this.assignments  = fileManager.loadAssignments();
        this.exams        = fileManager.loadExams();
        this.gradeRecords = fileManager.loadGrades();
        this.userGrades   = fileManager.loadUserGrades();
    }

    /** 모든 메모리 데이터를 초기화하고 파일에도 반영 */
    public void clearAll() {
        subjects.clear();
        assignments.clear();
        exams.clear();
        gradeRecords.clear();
        userGrades.clear();
        fileManager.saveSubjects(subjects);
        fileManager.saveAssignments(assignments);
        fileManager.saveExams(exams);
        fileManager.saveGrades(gradeRecords);
        fileManager.saveUserGrades(userGrades);
    }
}
