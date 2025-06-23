import common.init.ApplicationInitializer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import timetable.TimetableMainPanel;
import ui.panels.GradeCalculatorPanel;
import ui.panels.StudyPlanPanel;

public class MainApplication extends Application {
    
    private StudyPlanPanel studyPlanPanel;
    private GradeCalculatorPanel gradeCalculatorPanel;
    private TimetableMainPanel timetablePanel;
    private TabPane tabPane;
    private ApplicationInitializer initializer;
    
    @Override
    public void init() throws Exception {
        // JavaFX 초기화 전에 애플리케이션 초기화 수행
        initializer = new ApplicationInitializer();
        initializer.printApplicationInfo();
        initializer.initialize();
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🎨 UI 구성 요소 초기화 중...");
            
            // 각 패널 초기화
            studyPlanPanel = new StudyPlanPanel();
            gradeCalculatorPanel = new GradeCalculatorPanel();
            timetablePanel = new TimetableMainPanel();
            
            System.out.println("✅ 모든 패널 초기화 완료");
            
            // 탭 패널 생성
            tabPane = new TabPane();
            
            Tab timetableTab = new Tab("📅 시간표");
            timetableTab.setContent(timetablePanel);
            timetableTab.setClosable(false);
            
            Tab studyPlanTab = new Tab("📚 공부계획");
            studyPlanTab.setContent(studyPlanPanel);
            studyPlanTab.setClosable(false);
            
            Tab gradeCalculatorTab = new Tab("📊 학점계산");
            gradeCalculatorTab.setContent(gradeCalculatorPanel);
            gradeCalculatorTab.setClosable(false);
            
            tabPane.getTabs().addAll(
                timetableTab,
                studyPlanTab,
                gradeCalculatorTab
            );
            
            // 탭 변경 리스너 추가
            tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                if (newTab != null) {
                    String tabText = newTab.getText();
                    
                    // 개발 모드에서만 탭 변경 로그 출력
                    if (ApplicationInitializer.isDevelopmentMode()) {
                        System.out.println("🔄 탭 변경: " + tabText);
                    }
                    
                    // 탭 변경 시 해당 패널의 데이터 새로고침 (선택사항)
                    Platform.runLater(() -> {
                        try {
                            switch (tabText) {
                                case "📚 공부계획":
                                    if (studyPlanPanel != null) {
                                        studyPlanPanel.refreshData();
                                    }
                                    break;
                                case "📊 학점계산":
                                    if (gradeCalculatorPanel != null) {
                                        gradeCalculatorPanel.refreshData();
                                    }
                                    break;
                            }
                        } catch (Exception e) {
                            System.err.println("❌ 탭 변경 시 새로고침 오류: " + e.getMessage());
                        }
                    });
                }
            });
            
            Scene scene = new Scene(tabPane, 1200, 800);
            scene.getStylesheets().add("styles.css");
            
            primaryStage.setTitle("UniScheduler - 대학생 학습 스케줄러 v1.0");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            // 창 닫기 이벤트 처리
            primaryStage.setOnCloseRequest(e -> {
                System.out.println("🚪 애플리케이션 종료 요청...");
                cleanup();
                Platform.exit();
                System.exit(0); // 강제 종료 (데이터베이스 리스너 등을 확실히 정리)
            });
            
            primaryStage.show();
            
            // 시간표 탭을 첫 번째로 선택
            tabPane.getSelectionModel().selectFirst();
            
            // 성공 메시지 출력
            System.out.println("🚀 UniScheduler 애플리케이션 시작 완료!");
            System.out.println("💡 Tips:");
            System.out.println("   - 시간표에서 과목을 추가하면 자동으로 공부계획과 학점계산에 반영됩니다");
            System.out.println("   - 과목을 클릭하면 상세 정보를 볼 수 있습니다");
            System.out.println("   - 각 탭에서 데이터를 수정하면 실시간으로 다른 탭에 반영됩니다");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
        } catch (Exception e) {
            System.err.println("❌ 애플리케이션 시작 중 치명적 오류 발생:");
            e.printStackTrace();
            
            // 오류 다이얼로그 표시
            showCriticalErrorDialog(e);
            
            Platform.exit();
            System.exit(1);
        }
    }
    
    /**
     * 치명적 오류 다이얼로그 표시
     */
    private void showCriticalErrorDialog(Exception e) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR
            );
            alert.setTitle("치명적 오류");
            alert.setHeaderText("애플리케이션을 시작할 수 없습니다");
            alert.setContentText(
                "오류 내용: " + e.getMessage() + "\n\n" +
                "해결 방법:\n" +
                "1. Java 17 이상이 설치되어 있는지 확인하세요\n" +
                "2. JavaFX가 제대로 설정되어 있는지 확인하세요\n" +
                "3. data/ 폴더의 권한을 확인하세요\n" +
                "4. 문제가 지속되면 data/ 폴더를 삭제 후 재실행하세요"
            );
            alert.showAndWait();
        } catch (Exception dialogError) {
            System.err.println("❌ 오류 다이얼로그 표시 실패: " + dialogError.getMessage());
        }
    }
    
    /**
     * 애플리케이션 종료 시 리소스 정리
     */
    private void cleanup() {
        System.out.println("🧹 애플리케이션 리소스 정리 중...");
        
        try {
            // 각 패널의 리스너 해제
            if (studyPlanPanel != null) {
                studyPlanPanel.dispose();
                System.out.println("   ✅ StudyPlanPanel 정리 완료");
            }
        } catch (Exception e) {
            System.err.println("   ❌ StudyPlanPanel 정리 중 오류: " + e.getMessage());
        }
        
        try {
            if (gradeCalculatorPanel != null) {
                gradeCalculatorPanel.dispose();
                System.out.println("   ✅ GradeCalculatorPanel 정리 완료");
            }
        } catch (Exception e) {
            System.err.println("   ❌ GradeCalculatorPanel 정리 중 오류: " + e.getMessage());
        }
        
        try {
            // 애플리케이션 초기화 객체 정리
            if (initializer != null) {
                initializer.cleanup();
                System.out.println("   ✅ ApplicationInitializer 정리 완료");
            }
        } catch (Exception e) {
            System.err.println("   ❌ ApplicationInitializer 정리 중 오류: " + e.getMessage());
        }
        
        System.out.println("✅ 리소스 정리 완료");
    }
    
    /**
     * 모든 패널의 데이터를 강제 새로고침
     */
    public void refreshAllPanels() {
        Platform.runLater(() -> {
            try {
                if (studyPlanPanel != null) {
                    studyPlanPanel.refreshData();
                }
                if (gradeCalculatorPanel != null) {
                    gradeCalculatorPanel.refreshData();
                }
                System.out.println("🔄 모든 패널 데이터 새로고침 완료");
            } catch (Exception e) {
                System.err.println("❌ 전체 새로고침 중 오류: " + e.getMessage());
            }
        });
    }
    
    /**
     * 애플리케이션 정보 출력 (시작 시)
     */
    private void printStartupInfo() {
        System.out.println("🎯 실행 환경 정보:");
        System.out.println("   디버그 모드: " + (ApplicationInitializer.isDebugMode() ? "ON" : "OFF"));
        System.out.println("   개발 모드: " + (ApplicationInitializer.isDevelopmentMode() ? "ON" : "OFF"));
        System.out.println("   메모리 사용량: " + 
            (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " MB");
        System.out.println();
    }
    
    public static void main(String[] args) {
        // JVM 인수 처리
        for (String arg : args) {
            if ("--debug".equals(arg)) {
                System.setProperty("debug.mode", "true");
            } else if ("--dev".equals(arg)) {
                System.setProperty("dev.mode", "true");
            } else if ("--no-sample".equals(arg)) {
                System.setProperty("generate.sample.data", "false");
            }
        }
        
        // JavaFX 애플리케이션 시작
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("❌ 애플리케이션 시작 실패: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}