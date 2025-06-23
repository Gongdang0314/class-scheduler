// src/main/java/common/init/ApplicationInitializer.java
package common.init;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import common.database.DatabaseManager;

/**
 * 애플리케이션 초기화를 담당하는 클래스
 * 첫 실행 시 필요한 설정과 데이터를 준비합니다.
 */
public class ApplicationInitializer {
    
    private static final String DATA_DIR = "data/";
    private static final String CONFIG_FILE = DATA_DIR + "app_config.txt";
    
    private DatabaseManager dbManager;
    
    public ApplicationInitializer() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * 애플리케이션 초기화 실행
     */
    public void initialize() {
        System.out.println("🚀 UniScheduler 초기화 시작...");
        
        printSystemInfo();
        createDirectories();
        checkFirstRun();
        validateData();
        
        System.out.println("✅ UniScheduler 초기화 완료!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * 시스템 정보 출력
     */
    private void printSystemInfo() {
        System.out.println("🔧 시스템 정보:");
        System.out.println("   Java Version: " + System.getProperty("java.version"));
        System.out.println("   JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("   OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        System.out.println("   사용자: " + System.getProperty("user.name"));
        System.out.println("   작업 디렉토리: " + System.getProperty("user.dir"));
        System.out.println("   시작 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println();
    }
    
    /**
     * 필요한 디렉토리 생성
     */
    private void createDirectories() {
        File dataDir = new File(DATA_DIR);
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            if (created) {
                System.out.println("📁 데이터 디렉토리 생성: " + DATA_DIR);
            } else {
                System.err.println("❌ 데이터 디렉토리 생성 실패: " + DATA_DIR);
            }
        } else {
            System.out.println("📁 데이터 디렉토리 확인: " + DATA_DIR);
        }
        
        // 백업 디렉토리도 생성
        File backupDir = new File(DATA_DIR + "backups/");
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }
    }
    
    /**
     * 첫 실행인지 확인하고 필요한 초기 설정 수행
     */
    private void checkFirstRun() {
        File configFile = new File(CONFIG_FILE);
        
        if (!configFile.exists()) {
            System.out.println("🎉 첫 실행을 감지했습니다!");
            performFirstRunSetup();
            createConfigFile();
        } else {
            System.out.println("🔄 기존 설정을 불러왔습니다.");
        }
    }
    
    /**
     * 첫 실행 시 필요한 설정
     */
    private void performFirstRunSetup() {
        System.out.println("⚙️ 초기 설정을 수행합니다...");
        
        try {
            // 데이터베이스 초기 로드 테스트
            dbManager.getAllSubjects();
            dbManager.getAllAssignments();
            dbManager.getAllExams();
            
            
        } catch (Exception e) {
            System.err.println("❌ 초기 설정 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 설정 파일 생성
     */
    private void createConfigFile() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.createNewFile()) {
                System.out.println("📄 설정 파일 생성: " + CONFIG_FILE);
                
                // 기본 설정을 파일에 저장 (필요시)
                try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(configFile))) {
                    writer.println("# UniScheduler 설정 파일");
                    writer.println("created=" + LocalDateTime.now().toString());
                    writer.println("version=1.0");
                    writer.println("first_run=false");
                }
            }
        } catch (Exception e) {
            System.err.println("❌ 설정 파일 생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 데이터 유효성 검사
     */
    private void validateData() {
        System.out.println("🔍 데이터 유효성 검사 중...");
        
        try {
            // 기본적인 데이터 로드 테스트
            int subjectCount = dbManager.getAllSubjects().size();
            int assignmentCount = dbManager.getAllAssignments().size();
            int examCount = dbManager.getAllExams().size();
            
            System.out.println("📊 현재 데이터 현황:");
            System.out.println("   과목: " + subjectCount + "개");
            System.out.println("   과제: " + assignmentCount + "개");
            System.out.println("   시험: " + examCount + "개");
            
            // 데이터 일관성 검사
            validateDataConsistency();
            
            System.out.println("✅ 데이터 유효성 검사 통과");
            
        } catch (Exception e) {
            System.err.println("❌ 데이터 유효성 검사 실패: " + e.getMessage());
            handleDataValidationError(e);
        }
    }
    
    /**
     * 데이터 일관성 검사
     */
    private void validateDataConsistency() {
        // 과제의 과목 ID가 실제 존재하는지 검사
        long invalidAssignments = dbManager.getAllAssignments().stream()
            .filter(assignment -> dbManager.getSubjectById(assignment.getSubjectId()).isEmpty())
            .count();
        
        if (invalidAssignments > 0) {
            System.out.println("⚠️ 유효하지 않은 과제 " + invalidAssignments + "개 발견");
        }
        
        // 시험의 과목 ID가 실제 존재하는지 검사
        long invalidExams = dbManager.getAllExams().stream()
            .filter(exam -> dbManager.getSubjectById(exam.getSubjectId()).isEmpty())
            .count();
        
        if (invalidExams > 0) {
            System.out.println("⚠️ 유효하지 않은 시험 " + invalidExams + "개 발견");
        }
        
        // 시간표 충돌 검사
        checkTimetableConflicts();
    }
    
    /**
     * 시간표 충돌 검사
     */
    private void checkTimetableConflicts() {
        var subjects = dbManager.getAllSubjects();
        int conflicts = 0;
        
        for (int i = 0; i < subjects.size(); i++) {
            for (int j = i + 1; j < subjects.size(); j++) {
                var subject1 = subjects.get(i);
                var subject2 = subjects.get(j);
                
                if (hasTimeConflict(subject1, subject2)) {
                    System.out.println("⚠️ 시간표 충돌: " + subject1.getName() + " ↔ " + subject2.getName());
                    conflicts++;
                }
            }
        }
        
        if (conflicts == 0) {
            System.out.println("✅ 시간표 충돌 없음");
        } else {
            System.out.println("⚠️ 총 " + conflicts + "개의 시간표 충돌 발견");
        }
    }
    
    /**
     * 두 과목의 시간 충돌 검사
     */
    private boolean hasTimeConflict(common.model.Subject s1, common.model.Subject s2) {
        if (s1.getDayOfWeek() == null || s2.getDayOfWeek() == null) return false;
        if (!s1.getDayOfWeek().equals(s2.getDayOfWeek())) return false;
        if (s1.getStartTime() == null || s1.getEndTime() == null ||
            s2.getStartTime() == null || s2.getEndTime() == null) return false;
        
        return s1.getStartTime().compareTo(s2.getEndTime()) < 0 && 
               s2.getStartTime().compareTo(s1.getEndTime()) < 0;
    }
    
    /**
     * 데이터 유효성 검사 오류 처리
     */
    private void handleDataValidationError(Exception e) {
        System.err.println("🔧 데이터 문제를 자동으로 복구하려고 시도합니다...");
        
        try {
            // 백업 생성
            dbManager.createBackup();
            
            // 손상된 데이터 정리 (필요시)
            // cleanupCorruptedData();
            
            System.out.println("🔄 복구 시도 완료");
            
        } catch (Exception recoveryError) {
            System.err.println("❌ 자동 복구 실패: " + recoveryError.getMessage());
            System.err.println("💡 수동으로 data/ 폴더의 파일들을 확인하거나 삭제 후 재실행하세요.");
        }
    }
    
    /**
     * 디버그 모드 확인
     */
    public static boolean isDebugMode() {
        return "true".equals(System.getProperty("debug.mode", "false"));
    }
    
    /**
     * 개발 모드 확인
     */
    public static boolean isDevelopmentMode() {
        return "true".equals(System.getProperty("dev.mode", "false"));
    }
    
    /**
     * 애플리케이션 정보 출력
     */
    public void printApplicationInfo() {
        System.out.println("📱 UniScheduler - 대학생 학습 스케줄러");
        System.out.println("   버전: 1.0.0");
        System.out.println("   개발: Class Scheduler Team");
        System.out.println("   설명: 시간표, 공부계획, 학점계산을 통합 관리하는 애플리케이션");
        System.out.println();
        
        if (isDebugMode()) {
            System.out.println("🐛 디버그 모드 활성화");
        }
        
        if (isDevelopmentMode()) {
            System.out.println("🔧 개발 모드 활성화");
            System.out.println("   - 샘플 데이터 자동 생성");
            System.out.println("   - 상세 로그 출력");
        }
        
        System.out.println();
    }
    
    /**
     * 종료 시 정리 작업
     */
    public void cleanup() {
        System.out.println("🧹 애플리케이션 종료 준비...");
        
        try {
            // 최종 데이터 저장
            dbManager.saveAllData();
            
            // 종료 시간 기록
            System.out.println("⏰ 종료 시간: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
        } catch (Exception e) {
            System.err.println("❌ 종료 처리 중 오류: " + e.getMessage());
        }
        
        System.out.println("👋 UniScheduler를 이용해 주셔서 감사합니다!");
    }
}