// src/main/java/studyschedule/StudyTracker.java
package studyschedule;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.panels.StudyPlanPanel;

public class StudyTracker extends Application {

    @Override
    public void start(Stage primaryStage) {
        // StudyPlanPanel: 공부 계획 UI + 반영(공부 시간) 기능을 모두 포함합니다.
        StudyPlanPanel studyPlanPanel = new StudyPlanPanel();

        // 원하는 크기로 Scene 설정
        Scene scene = new Scene(studyPlanPanel, 900, 600);

        primaryStage.setTitle("Study Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // JavaFX 애플리케이션 런처
        launch(args);
    }
}
