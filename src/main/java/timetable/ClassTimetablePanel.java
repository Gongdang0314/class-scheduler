package timetable;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import model.Subject;

public class ClassTimetablePanel extends GridPane {
    public ClassTimetablePanel() {
        setHgap(10);
        setVgap(10);

        // 요일 헤더
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        for (int i = 0; i < days.length; i++) {
            add(new Label(days[i]), i + 1, 0);
        }

        // 시간 헤더
        for (int hour = 9; hour <= 18; hour++) {
            add(new Label(hour + ":00"), 0, hour - 8);
        }

        // TODO: 수업 추가 및 클릭 이벤트는 여기에
        TimetableManager manager = new TimetableManager();

        // 테스트용 수업 추가
        Subject math = new Subject("수학", "Mon", 10, 12);
        if (manager.addSubject(math)) {
            Label label = new Label(math.getName());
            add(label, 1, 10 - 8); // "Mon"은 인덱스 1, 10시는 2행
        }
    }
}
