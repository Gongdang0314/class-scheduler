import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import ui.panels.TimetablePanel;
import ui.panels.AssignmentExamPanel;
import ui.panels.GradeCalculatorPanel;
import ui.panels.StudyPlanPanel;

public class MainApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();
        
        Tab studyPlanTab = new Tab("📚 공부계획");
        studyPlanTab.setContent(new StudyPlanPanel());
        studyPlanTab.setClosable(false);
        
        Tab gradeCalculatorTab = new Tab("📊 학점계산");
        gradeCalculatorTab.setContent(new GradeCalculatorPanel());
        gradeCalculatorTab.setClosable(false);
        
        Tab timetableTab = new Tab("📅 시간표");
        timetableTab.setContent(new timetable.TimetableMainPanel());
        timetableTab.setClosable(false);
        
        tabPane.getTabs().addAll(
            timetableTab,
            studyPlanTab,
            gradeCalculatorTab
        );
        
        Scene scene = new Scene(tabPane, 1200, 800);
        scene.getStylesheets().add("styles.css");
        
        primaryStage.setTitle("UniScheduler - 대학생 학습 스케줄러");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.show();
        
        tabPane.getSelectionModel().selectFirst();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
