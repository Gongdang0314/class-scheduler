// src/main/java/ui/panels/StudyPlanPanel.java
package ui.panels;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.database.DatabaseManager;
import common.model.Subject;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

public class StudyPlanPanel extends VBox {

    private static final String DATA_DIR  = "data/";
    private static final String PLAN_FILE = "study_plans.txt";

    private final DatabaseManager dbManager;

    private final TableView<StudyPlanItem> planTable;
    private final TextField               subjectField;
    private final TextField               hoursField;
    private final DatePicker              targetDatePicker;
    private final ComboBox<String>        priorityComboBox;
    private final TextField               studiedHoursField;
    private final Button                  reflectButton;

    public StudyPlanPanel() {
        dbManager = DatabaseManager.getInstance();

        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");

        // --- 상단 폼 필드
        subjectField     = UIStyleManager.createStandardTextField("과목명");
        subjectField.setEditable(false);

        hoursField       = UIStyleManager.createStandardTextField("목표 시간");
        targetDatePicker = UIStyleManager.createStandardDatePicker();

        priorityComboBox = UIStyleManager.createStandardComboBox();
        priorityComboBox.getItems().addAll("높음", "보통", "낮음");
        priorityComboBox.setValue("보통");

        studiedHoursField = UIStyleManager.createStandardTextField("공부한 시간");

        reflectButton = UIStyleManager.createPrimaryButton("최신화");

        // --- 테이블
        planTable = UIStyleManager.createStandardTableView();
        initTable();

        layoutComponents();
        hookHandlers();

        loadPlans();
    }

    private void initTable() {
        planTable.getColumns().clear();

        TableColumn<StudyPlanItem,String> c1 = new TableColumn<>("과목");
        TableColumn<StudyPlanItem,String> c2 = new TableColumn<>("목표시간");
        TableColumn<StudyPlanItem,String> c3 = new TableColumn<>("목표일");
        TableColumn<StudyPlanItem,String> c4 = new TableColumn<>("우선순위");
        TableColumn<StudyPlanItem,String> c5 = new TableColumn<>("진행상태");

        c1.setCellValueFactory(new PropertyValueFactory<>("subject"));
        c2.setCellValueFactory(new PropertyValueFactory<>("hours"));
        c3.setCellValueFactory(new PropertyValueFactory<>("date"));
        c4.setCellValueFactory(new PropertyValueFactory<>("priority"));
        c5.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 가운데 정렬
        c1.setStyle("-fx-alignment: CENTER;");
        c2.setStyle("-fx-alignment: CENTER;");
        c3.setStyle("-fx-alignment: CENTER;");
        c4.setStyle("-fx-alignment: CENTER;");
        c5.setStyle("-fx-alignment: CENTER;");

        planTable.getColumns().addAll(c1, c2, c3, c4, c5);
        planTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void layoutComponents() {
        Label title = UIStyleManager.createTitleLabel("📚 공부 계획 관리");

        GridPane form = UIStyleManager.createFormGrid();
        form.setVgap(10);

        form.add(new Label("과목"),         0, 0);
        form.add(subjectField,             1, 0);
        form.add(new Label("목표시간"),     0, 1);
        form.add(hoursField,               1, 1);
        form.add(new Label("목표일"),       0, 2);
        form.add(targetDatePicker,         1, 2);
        form.add(new Label("우선순위"),     0, 3);
        form.add(priorityComboBox,         1, 3);
        form.add(new Label("공부한 시간"),  0, 4);
        form.add(studiedHoursField,        1, 4);
        form.add(reflectButton,            1, 5);

        GridPane.setHgrow(subjectField,      Priority.ALWAYS);
        GridPane.setHgrow(hoursField,        Priority.ALWAYS);
        GridPane.setHgrow(priorityComboBox,  Priority.ALWAYS);
        GridPane.setHgrow(studiedHoursField, Priority.ALWAYS);

        this.getChildren().addAll(title, form, planTable);
    }

    private void hookHandlers() {
        planTable.setOnMouseClicked((MouseEvent e) -> {
            StudyPlanItem sel = planTable.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            subjectField.setText(sel.getSubject());
            hoursField.setText(sel.getHours());
            targetDatePicker.setValue(
                sel.getDate() != null ? LocalDate.parse(sel.getDate()) : null
            );
            priorityComboBox.setValue(
                sel.getPriority() != null ? sel.getPriority() : "보통"
            );
            studiedHoursField.setText(
                extractStudiedHours(sel.getStatus()) + ""
            );
        });

        reflectButton.setOnAction(e -> {
            StudyPlanItem sel = planTable.getSelectionModel().getSelectedItem();
            if (sel == null) return;

            String hours = hoursField.getText().trim();
            LocalDate date = targetDatePicker.getValue();
            String prio = priorityComboBox.getValue();
            int studied = parseIntOrZero(studiedHoursField.getText());

            sel.setHours(hours.isEmpty() ? "0" : hours);
            sel.setDate(date != null ? date.toString() : null);
            sel.setPriority(prio);

            int target = parseIntOrZero(sel.getHours());
            int total = Math.min(studied, target);
            sel.setStatus(total + "h / " + target + "h");

            planTable.refresh();
            savePlans();
        });
    }

    private void loadPlans() {
        Map<String,StudyPlanItem> planMap = new HashMap<>();
        File file = new File(DATA_DIR + PLAN_FILE);
        if (file.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] p = line.split("\\|", -1);
                    String subj     = p[0].isEmpty() ? null : p[0];
                    String hours    = p[1].isEmpty() ? "0"  : p[1];
                    String date     = p[2].isEmpty() ? null : p[2];
                    String priority = p[3].isEmpty() ? null : p[3];
                    String status   = p[4].isEmpty() ? "0/0": p[4];
                    if (subj != null) {
                        planMap.put(subj,
                            new StudyPlanItem(subj, hours, date, priority, status)
                        );
                    }
                }
            } catch (IOException ex) {
                System.err.println("❌ loadPlans 실패: " + ex.getMessage());
            }
        }

        List<StudyPlanItem> merged = new ArrayList<>();
        for (Subject s : dbManager.getAllSubjects()) {
            String name = s.getName();
            if (planMap.containsKey(name)) {
                merged.add(planMap.get(name));
            } else {
                merged.add(new StudyPlanItem(
                    name, "0", null, "보통", "0/0"
                ));
            }
        }
        planTable.getItems().setAll(merged);
    }

    private void savePlans() {
        new File(DATA_DIR).mkdirs();
        try (PrintWriter w = new PrintWriter(new FileWriter(DATA_DIR + PLAN_FILE))) {
            for (StudyPlanItem it : planTable.getItems()) {
                w.println(String.join("|",
                    it.getSubject()  == null ? "" : it.getSubject(),
                    it.getHours()    == null ? "0": it.getHours(),
                    it.getDate()     == null ? "" : it.getDate(),
                    it.getPriority() == null ? "" : it.getPriority(),
                    it.getStatus()   == null ? "0/0": it.getStatus()
                ));
            }
        } catch (IOException ex) {
            System.err.println("❌ savePlans 실패: " + ex.getMessage());
        }
    }

    private int parseIntOrZero(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return 0; }
    }

    private int extractStudiedHours(String status) {
        if (status == null || !status.contains("/")) return 0;
        return parseIntOrZero(status.split("/", 2)[0]);
    }

    public static class StudyPlanItem {
        private String subject, hours, date, priority, status;
        public StudyPlanItem(String s, String h, String d, String p, String st) {
            subject  = s; hours    = h; date     = d; priority = p; status   = st;
        }
        public String getSubject()  { return subject; }
        public String getHours()    { return hours;   }
        public String getDate()     { return date;    }
        public String getPriority() { return priority;}
        public String getStatus()   { return status;  }
        public void   setHours(String h)    { hours    = h;  }
        public void   setDate(String d)     { date     = d;  }
        public void   setPriority(String p) { priority = p;  }
        public void   setStatus(String st)  { status   = st; }
    }
}
