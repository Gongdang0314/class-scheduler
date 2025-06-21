package timetable;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.java.common.model.Subject;
import main.java.common.database.FileManager;

import java.util.List;

public class TimetablePanel extends BorderPane {
    private GridPane timetableGrid;
    private TimetableManager manager;

    public TimetablePanel() {
        this.manager = new TimetableManager();
        this.timetableGrid = new GridPane();
        timetableGrid.setHgap(10);
        timetableGrid.setVgap(10);

        drawHeaders();
        loadAndDisplaySubjects();

        setCenter(timetableGrid);
    }

    private void drawHeaders() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        for (int i = 0; i < days.length; i++) {
            timetableGrid.add(new Label(days[i]), i + 1, 0);
        }
        for (int hour = 9; hour <= 20; hour++) {
            timetableGrid.add(new Label(hour + ":00"), 0, hour - 8);
        }
    }

    private void loadAndDisplaySubjects() {
        FileManager fileManager = new FileManager();
        List<Subject> subjects = fileManager.loadSubjects();

        for (Subject subject : subjects) {
            addSubjectToGrid(subject);
        }
    }

    private void addSubjectToGrid(Subject subject) {
        int col = dayToColumn(subject.getDayOfWeek());
        int startRow = hourToRow(subject.getStartTime());
        int endRow = hourToRow(subject.getEndTime());
        int span = endRow - startRow;

        Label subjectLabel = new Label(subject.getName() + "\n(" + subject.getStartTime() + "~" + subject.getEndTime() + ")");
        subjectLabel.setStyle("-fx-background-color: lightblue; -fx-border-color: gray;");
        subjectLabel.setFont(new Font("Arial", 13));
        subjectLabel.setMaxWidth(Double.MAX_VALUE);
        subjectLabel.setWrapText(true);

        subjectLabel.setOnMouseClicked((MouseEvent e) -> {
            System.out.println("Clicked subject: " + subject.getName());
            // 여기에 과제/시험 탭 연동 기능 추가 가능
        });

        timetableGrid.add(subjectLabel, col, startRow, 1, span);
    }

    private int dayToColumn(String day) {
        return switch (day) {
            case "Mon" -> 1;
            case "Tue" -> 2;
            case "Wed" -> 3;
            case "Thu" -> 4;
            case "Fri" -> 5;
            default -> 1;
        };
    }

    private int hourToRow(String time) {
        String[] parts = time.split(":");
        int hour = Integer.parseInt(parts[0]);
        return hour - 8; // 9:00 => 1행
    }
}
