package timetable;

import main.java.common.model.Subject;
import main.java.common.database.FileManager;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
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
        for (int hour = 9; hour <= 18; hour++) {
            timetableGrid.add(new Label(hour + ":00"), 0, hour - 8);
        }
    }

    private void loadAndDisplaySubjects() {
        FileManager fileManager = new FileManager();
        List<Subject> subjects = fileManager.loadSubjects();

        for (Subject subject : subjects) {
            if (manager.addSubject(subject)) {
                int col = getDayColumnIndex(subject.getDayOfWeek());
                int row = getHourRowIndex(subject.getStartTime());
                Label label = new Label(subject.getName());
                timetableGrid.add(label, col, row);
            }
        }
    }

    private int getDayColumnIndex(String day) {
        return switch (day) {
            case "Mon" -> 1;
            case "Tue" -> 2;
            case "Wed" -> 3;
            case "Thu" -> 4;
            case "Fri" -> 5;
            default -> 0;
        };
    }

    private int getHourRowIndex(String time) {
        try {
            int hour = Integer.parseInt(time.split(":")[0]);
            return hour - 8;
        } catch (Exception e) {
            return 0;
        }
    }
}
