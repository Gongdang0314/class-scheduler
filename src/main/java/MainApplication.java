import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import timetable.ClassTimetablePanel;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        Tab studyPlanTab = new Tab("공부계획");
        Tab gradeCalcTab = new Tab("학점계산");
        Tab timetableTab = new Tab("시간표");
        Tab taskExamTab = new Tab("과제/시험");

        // 시간표 탭에 시간표 패널 연결
        timetableTab.setContent(new ClassTimetablePanel());

        tabPane.getTabs().addAll(
            studyPlanTab,
            gradeCalcTab,
            timetableTab,
            taskExamTab
        );

        Scene scene = new Scene(tabPane, 1000, 700);
        scene.getStylesheets().add("styles.css");

        primaryStage.setTitle("UniScheduler");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
