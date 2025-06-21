import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        tabPane.getTabs().addAll(
            new Tab("공부계획"),
            new Tab("학점계산"),
            new Tab("시간표"),
            new Tab("과제/시험")
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
