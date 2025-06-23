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
    
    @Override
    public void start(Stage primaryStage) {
        // 각 패널 초기화
        studyPlanPanel = new StudyPlanPanel();
        gradeCalculatorPanel = new GradeCalculatorPanel();
        timetablePanel = new TimetableMainPanel();
        
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
        
        // 탭 변경 리스너 추가 (옵션: 탭 변경 시 데이터 새로고침)
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                String tabText = newTab.getText();
                System.out.println("🔄 탭 변경: " + tabText);
                
                // 탭 변경 시 해당 패널의 데이터 새로고침 (선택사항)
                Platform.runLater(() -> {
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
                });
            }
        });
        
        Scene scene = new Scene(tabPane, 1200, 800);
        scene.getStylesheets().add("styles.css");
        
        primaryStage.setTitle("UniScheduler - 대학생 학습 스케줄러");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        
        // 창 닫기 이벤트 처리 (리스너 정리)
        primaryStage.setOnCloseRequest(e -> {
            cleanup();
            Platform.exit();
        });
        
        primaryStage.show();
        
        // 시간표 탭을 첫 번째로 선택
        tabPane.getSelectionModel().selectFirst();
        
        System.out.println("🚀 UniScheduler 애플리케이션 시작 완료");
        System.out.println("💡 시간표에서 과목을 추가하면 자동으로 공부계획과 학점계산에 반영됩니다!");
    }
    
    /**
     * 애플리케이션 종료 시 리소스 정리
     */
    private void cleanup() {
        System.out.println("🧹 애플리케이션 리소스 정리 중...");
        
        try {
            if (studyPlanPanel != null) {
                studyPlanPanel.dispose();
            }
        } catch (Exception e) {
            System.err.println("❌ StudyPlanPanel 정리 중 오류: " + e.getMessage());
        }
        
        try {
            if (gradeCalculatorPanel != null) {
                gradeCalculatorPanel.dispose();
            }
        } catch (Exception e) {
            System.err.println("❌ GradeCalculatorPanel 정리 중 오류: " + e.getMessage());
        }
        
        System.out.println("✅ 리소스 정리 완료");
    }
    
    /**
     * 모든 패널의 데이터를 강제 새로고침
     */
    public void refreshAllPanels() {
        Platform.runLater(() -> {
            if (studyPlanPanel != null) {
                studyPlanPanel.refreshData();
            }
            if (gradeCalculatorPanel != null) {
                gradeCalculatorPanel.refreshData();
            }
            System.out.println("🔄 모든 패널 데이터 새로고침 완료");
        });
    }
    
    public static void main(String[] args) {
        // JVM 설정 출력 (디버깅용)
        System.out.println("🔧 Java Version: " + System.getProperty("java.version"));
        System.out.println("🔧 JavaFX Version: " + System.getProperty("javafx.version"));
        System.out.println("🔧 OS: " + System.getProperty("os.name"));
        
        launch(args);
    }
}