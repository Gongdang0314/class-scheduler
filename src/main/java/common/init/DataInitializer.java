package common.init;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import common.database.DatabaseManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * 데이터 초기화를 안전하게 수행하는 클래스
 * 백업 생성 후 데이터를 초기화하는 기능을 제공합니다.
 */
public class DataInitializer {
    
    private static final String DATA_DIR = "data/";
    private static final String BACKUP_DIR = DATA_DIR + "backups/";
    
    private DatabaseManager dbManager;
    
    public DataInitializer() {
        this.dbManager = DatabaseManager.getInstance();
        createBackupDirectory();
    }
    
    /**
     * 백업 디렉토리 생성
     */
    private void createBackupDirectory() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            boolean created = backupDir.mkdirs();
            if (created) {
                System.out.println("📁 백업 디렉토리 생성: " + BACKUP_DIR);
            }
        }
    }
    
    /**
     * 전체 데이터 초기화 (확인 다이얼로그 포함)
     * @return 초기화 성공 여부
     */
    public boolean initializeAllDataWithConfirmation() {
        // 확인 다이얼로그
        Alert confirmation = new Alert(Alert.AlertType.WARNING);
        confirmation.setTitle("⚠️ 데이터 초기화");
        confirmation.setHeaderText("모든 데이터를 삭제하시겠습니까?");
        confirmation.setContentText(
            "다음 데이터가 모두 삭제됩니다:\n" +
            "• 시간표 정보 (과목, 시간)\n" +
            "• 과제 및 시험 정보\n" +
            "• 성적 정보\n" +
            "• 공부 계획 정보\n\n" +
            "⚠️ 이 작업은 되돌릴 수 없습니다!\n" +
            "계속하기 전에 자동으로 백업이 생성됩니다."
        );
        
        ButtonType backupAndDelete = new ButtonType("백업 후 삭제");
        ButtonType forceDelete = new ButtonType("강제 삭제");
        ButtonType cancel = new ButtonType("취소", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        
        confirmation.getButtonTypes().setAll(backupAndDelete, forceDelete, cancel);
        
        java.util.Optional<ButtonType> result = confirmation.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == backupAndDelete) {
                return initializeAllDataWithBackup();
            } else if (result.get() == forceDelete) {
                return initializeAllDataForced();
            }
        }
        
        return false; // 취소된 경우
    }
    
    /**
     * 백업 생성 후 데이터 초기화
     */
    public boolean initializeAllDataWithBackup() {
        try {
            System.out.println("🔄 데이터 초기화 시작 (백업 포함)...");
            
            // 1. 백업 생성
            if (createFullBackup()) {
                System.out.println("✅ 백업 생성 완료");
            } else {
                System.err.println("❌ 백업 생성 실패 - 초기화 중단");
                showAlert("백업 실패", "백업 생성에 실패했습니다. 안전을 위해 초기화를 중단합니다.");
                return false;
            }
            
            // 2. 데이터 초기화
            boolean success = performDataInitialization();
            
            if (success) {
                showAlert("초기화 완료", 
                    "✅ 모든 데이터가 성공적으로 초기화되었습니다!\n\n" +
                    "백업은 다음 위치에 저장되었습니다:\n" + BACKUP_DIR);
                System.out.println("🎉 데이터 초기화 완료!");
            } else {
                showAlert("초기화 실패", "데이터 초기화 중 오류가 발생했습니다.");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ 데이터 초기화 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            showAlert("오류", "데이터 초기화 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 강제 데이터 초기화 (백업 없이)
     */
    public boolean initializeAllDataForced() {
        try {
            System.out.println("⚠️ 강제 데이터 초기화 시작...");
            
            boolean success = performDataInitialization();
            
            if (success) {
                showAlert("강제 초기화 완료", "⚠️ 모든 데이터가 강제로 삭제되었습니다!");
                System.out.println("⚠️ 강제 데이터 초기화 완료!");
            } else {
                showAlert("초기화 실패", "강제 데이터 초기화 중 오류가 발생했습니다.");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ 강제 데이터 초기화 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            showAlert("오류", "강제 데이터 초기화 중 예상치 못한 오류가 발생했습니다: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 실제 데이터 초기화 수행
     */
    private boolean performDataInitialization() {
        try {
            // 1. DatabaseManager를 통한 메모리 데이터 초기화
            dbManager.clearAllData();
            
            // 2. 파일 시스템 데이터 초기화
            clearDataFiles();
            
            // 3. 설정 파일 재생성
            resetConfigFile();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 데이터 초기화 수행 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 전체 백업 생성
     */
    private boolean createFullBackup() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupPrefix = "backup_" + timestamp + "_";
            
            System.out.println("📦 백업 생성 중: " + timestamp);
            
            // 각 데이터 파일 백업
            String[] dataFiles = {
                "subjects.txt",
                "assignments.txt", 
                "exams.txt",
                "grades.txt",
                "user_grades.txt",
                "study_plans.txt",
                "app_config.txt"
            };
            
            int successCount = 0;
            for (String fileName : dataFiles) {
                if (backupFile(fileName, backupPrefix + fileName)) {
                    successCount++;
                } else {
                    System.err.println("⚠️ 파일 백업 실패: " + fileName);
                }
            }
            
            System.out.println("📦 백업 완료: " + successCount + "/" + dataFiles.length + " 파일");
            return successCount > 0; // 하나라도 백업되면 성공으로 간주
            
        } catch (Exception e) {
            System.err.println("❌ 백업 생성 중 오류: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 개별 파일 백업
     */
    private boolean backupFile(String fileName, String backupFileName) {
        try {
            File sourceFile = new File(DATA_DIR + fileName);
            if (!sourceFile.exists()) {
                System.out.println("⏭️ 파일 없음 (백업 스킵): " + fileName);
                return true; // 파일이 없는 것은 오류가 아님
            }
            
            File backupFile = new File(BACKUP_DIR + backupFileName);
            
            // 파일 복사
            java.nio.file.Files.copy(
                sourceFile.toPath(), 
                backupFile.toPath(), 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            
            System.out.println("✅ 백업 완료: " + fileName + " → " + backupFileName);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ 파일 백업 실패 (" + fileName + "): " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 데이터 파일들 삭제
     */
    private void clearDataFiles() {
        String[] dataFiles = {
            "subjects.txt",
            "assignments.txt", 
            "exams.txt",
            "grades.txt",
            "user_grades.txt",
            "study_plans.txt"
        };
        
        for (String fileName : dataFiles) {
            File file = new File(DATA_DIR + fileName);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("🗑️ 파일 삭제: " + fileName);
                } else {
                    System.err.println("❌ 파일 삭제 실패: " + fileName);
                }
            }
        }
    }
    
    /**
     * 설정 파일 재생성
     */
    private void resetConfigFile() {
        try {
            File configFile = new File(DATA_DIR + "app_config.txt");
            if (configFile.exists()) {
                configFile.delete();
            }
            
            // 새로운 설정 파일 생성
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(configFile))) {
                writer.println("# UniScheduler 설정 파일 (초기화됨)");
                writer.println("created=" + LocalDateTime.now().toString());
                writer.println("version=1.0");
                writer.println("first_run=true");
                writer.println("initialized_at=" + LocalDateTime.now().toString());
            }
            
            System.out.println("⚙️ 설정 파일 재생성 완료");
            
        } catch (Exception e) {
            System.err.println("❌ 설정 파일 재생성 실패: " + e.getMessage());
        }
    }
    
    /**
     * 샘플 데이터 생성
     */
    public void generateSampleData() {
        try {
            System.out.println("📝 샘플 데이터 생성 중...");
            
            // 샘플 과목 추가
            common.model.Subject subject1 = new common.model.Subject();
            subject1.setName("자바 프로그래밍");
            subject1.setCredits(3);
            subject1.setProfessor("김교수");
            subject1.setClassroom("IT101");
            subject1.setCategory("전공필수");
            subject1.setDayOfWeek("월");
            subject1.setStartTime("09:00");
            subject1.setEndTime("10:30");
            
            common.model.Subject subject2 = new common.model.Subject();
            subject2.setName("데이터베이스");
            subject2.setCredits(3);
            subject2.setProfessor("이교수");
            subject2.setClassroom("IT102");
            subject2.setCategory("전공필수");
            subject2.setDayOfWeek("화");
            subject2.setStartTime("10:00");
            subject2.setEndTime("11:30");
            
            dbManager.addSubject(subject1);
            dbManager.addSubject(subject2);
            
            System.out.println("✅ 샘플 데이터 생성 완료");
            showAlert("샘플 데이터", "샘플 과목 2개가 추가되었습니다!");
            
        } catch (Exception e) {
            System.err.println("❌ 샘플 데이터 생성 실패: " + e.getMessage());
            showAlert("오류", "샘플 데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 백업 목록 조회
     */
    public String[] getBackupList() {
        File backupDir = new File(BACKUP_DIR);
        if (!backupDir.exists()) {
            return new String[0];
        }
        
        File[] backupFiles = backupDir.listFiles((dir, name) -> name.startsWith("backup_"));
        if (backupFiles == null) {
            return new String[0];
        }
        
        java.util.Arrays.sort(backupFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        
        return java.util.Arrays.stream(backupFiles)
            .map(File::getName)
            .toArray(String[]::new);
    }
    
    /**
     * 데이터베이스 상태 확인
     */
    public String getDatabaseStatus() {
        return dbManager.getDatabaseStatus();
    }
    
    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}