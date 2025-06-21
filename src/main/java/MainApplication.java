import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import ui.panels.AssignmentExamPanel;
import ui.panels.GradeCalculatorPanel;
import ui.panels.StudyPlanPanel;
import ui.panels.TimetablePanel;

public class MainApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        
        // 각 탭에 해당하는 패널 생성
        Tab studyPlanTab = new Tab("📚 공부계획");
        studyPlanTab.setContent(new StudyPlanPanel());
        studyPlanTab.setClosable(false);
        
        Tab gradeCalculatorTab = new Tab("📊 학점계산");
        gradeCalculatorTab.setContent(new GradeCalculatorPanel());
        gradeCalculatorTab.setClosable(false);
        
        Tab timetableTab = new Tab("📅 시간표");
        timetableTab.setContent(new TimetablePanel());
        timetableTab.setClosable(false);
        
        Tab assignmentExamTab = new Tab("📝 과제/시험");
        assignmentExamTab.setContent(new AssignmentExamPanel());
        assignmentExamTab.setClosable(false);
        
        // 탭 추가
        tabPane.getTabs().addAll(
            studyPlanTab,
            gradeCalculatorTab,
            timetableTab,
            assignmentExamTab
        );
        
        // 씬 설정
        Scene scene = new Scene(tabPane, 1200, 800);
        scene.getStylesheets().add("styles.css");
        
        // 스테이지 설정
        primaryStage.setTitle("UniScheduler - 대학생 학습 스케줄러");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
        
        // 초기 탭 선택
        tabPane.getSelectionModel().selectFirst();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}