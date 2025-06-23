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
import common.listeners.DataChangeListener;
import common.model.Subject;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

public class StudyPlanPanel extends VBox implements DataChangeListener {

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
    private final Button                  addButton;
    private final Button                  deleteButton;
    private final Label                   statusLabel;

    public StudyPlanPanel() {
        dbManager = DatabaseManager.getInstance();
        
        // 데이터 변경 리스너로 등록
        dbManager.addDataChangeListener(this);

        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");

        // --- 상단 폼 필드
        subjectField = UIStyleManager.createStandardTextField("과목명");
        subjectField.setEditable(false);

        hoursField = UIStyleManager.createStandardTextField("목표 시간 (h)");
        targetDatePicker = UIStyleManager.createStandardDatePicker();
        targetDatePicker.setValue(LocalDate.now().plusWeeks(1)); // 기본값: 1주일 후

        priorityComboBox = UIStyleManager.createStandardComboBox();
        priorityComboBox.getItems().addAll("높음", "보통", "낮음");
        priorityComboBox.setValue("보통");

        studiedHoursField = UIStyleManager.createStandardTextField("공부한 시간 (h)");

        reflectButton = UIStyleManager.createPrimaryButton("반영하기");
        addButton = UIStyleManager.createPrimaryButton("계획 추가");
        deleteButton = UIStyleManager.createSecondaryButton("계획 삭제");
        
        statusLabel = UIStyleManager.createSubLabel("📊 총 계획: 0개 | 완료: 0개 | 진행률: 0%");

        // --- 테이블
        planTable = UIStyleManager.createStandardTableView();
        initTable();

        layoutComponents();
        hookHandlers();

        // 초기 데이터 로드
        refreshData();
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

        // 컬럼 너비 설정
        c1.setPrefWidth(150);
        c2.setPrefWidth(100);
        c3.setPrefWidth(120);
        c4.setPrefWidth(100);
        c5.setPrefWidth(150);

        // 가운데 정렬
        c1.setStyle("-fx-alignment: CENTER;");
        c2.setStyle("-fx-alignment: CENTER;");
        c3.setStyle("-fx-alignment: CENTER;");
        c4.setStyle("-fx-alignment: CENTER;");
        c5.setStyle("-fx-alignment: CENTER;");

        planTable.getColumns().addAll(c1, c2, c3, c4, c5);
        planTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        planTable.setPrefHeight(300);
    }

    private void layoutComponents() {
        Label title = UIStyleManager.createTitleLabel("📚 공부 계획 관리");

        // 상태 정보
        HBox statusBox = UIStyleManager.createStandardHBox();
        statusBox.getChildren().add(statusLabel);

        GridPane form = UIStyleManager.createFormGrid();
        form.setVgap(10);

        form.add(new Label("과목:"),         0, 0);
        form.add(subjectField,              1, 0);
        form.add(new Label("목표시간(h):"),  0, 1);
        form.add(hoursField,                1, 1);
        form.add(new Label("목표일:"),       0, 2);
        form.add(targetDatePicker,          1, 2);
        form.add(new Label("우선순위:"),     0, 3);
        form.add(priorityComboBox,          1, 3);
        form.add(new Label("공부한시간(h):"), 0, 4);
        form.add(studiedHoursField,         1, 4);

        // 버튼 영역
        HBox buttonBox = UIStyleManager.createStandardHBox();
        buttonBox.getChildren().addAll(reflectButton, addButton, deleteButton);

        form.add(buttonBox, 1, 5);

        GridPane.setHgrow(subjectField,      Priority.ALWAYS);
        GridPane.setHgrow(hoursField,        Priority.ALWAYS);
        GridPane.setHgrow(priorityComboBox,  Priority.ALWAYS);
        GridPane.setHgrow(studiedHoursField, Priority.ALWAYS);

        this.getChildren().addAll(title, statusBox, form, planTable);
    }

    private void hookHandlers() {
        // 테이블 클릭 시 폼에 데이터 로드
        planTable.setOnMouseClicked((MouseEvent e) -> {
            StudyPlanItem sel = planTable.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            
            subjectField.setText(sel.getSubject());
            hoursField.setText(sel.getHours());
            
            try {
                if (sel.getDate() != null && !sel.getDate().isEmpty()) {
                    targetDatePicker.setValue(LocalDate.parse(sel.getDate()));
                } else {
                    targetDatePicker.setValue(null);
                }
            } catch (Exception ex) {
                targetDatePicker.setValue(null);
            }
            
            priorityComboBox.setValue(
                sel.getPriority() != null ? sel.getPriority() : "보통"
            );
            
            studiedHoursField.setText(
                String.valueOf(extractStudiedHours(sel.getStatus()))
            );
        });

        // 반영하기 버튼 (기존 계획 수정)
        reflectButton.setOnAction(e -> {
            StudyPlanItem sel = planTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("선택 오류", "수정할 계획을 선택해주세요!");
                return;
            }

            updateSelectedPlan(sel);
        });
        
        // 계획 추가 버튼 (새로운 계획 추가)
        addButton.setOnAction(e -> {
            String subject = subjectField.getText().trim();
            if (subject.isEmpty()) {
                showAlert("입력 오류", "과목명을 입력해주세요!");
                return;
            }
            
            // 이미 존재하는 과목인지 확인
            boolean exists = planTable.getItems().stream()
                .anyMatch(item -> item.getSubject().equals(subject));
                
            if (exists) {
                showAlert("중복 오류", "이미 등록된 과목입니다. 기존 계획을 수정해주세요.");
                return;
            }
            
            addNewPlan();
        });
        
        // 계획 삭제 버튼
        deleteButton.setOnAction(e -> {
            StudyPlanItem sel = planTable.getSelectionModel().getSelectedItem();
            if (sel == null) {
                showAlert("선택 오류", "삭제할 계획을 선택해주세요!");
                return;
            }
            
            planTable.getItems().remove(sel);
            savePlans();
            updateStatusLabel();
            clearForm();
            showAlert("성공", "계획이 삭제되었습니다!");
        });
    }
    
    private void updateSelectedPlan(StudyPlanItem item) {
        try {
            String hours = hoursField.getText().trim();
            LocalDate date = targetDatePicker.getValue();
            String prio = priorityComboBox.getValue();
            int studied = parseIntOrZero(studiedHoursField.getText());

            item.setHours(hours.isEmpty() ? "0" : hours);
            item.setDate(date != null ? date.toString() : null);
            item.setPriority(prio);

            int target = parseIntOrZero(item.getHours());
            int total = Math.min(studied, target);
            item.setStatus(total + "h / " + target + "h");

            planTable.refresh();
            savePlans();
            updateStatusLabel();
            showAlert("성공", "계획이 업데이트되었습니다!");
            
        } catch (Exception ex) {
            showAlert("오류", "계획 업데이트 중 오류가 발생했습니다: " + ex.getMessage());
        }
    }
    
    private void addNewPlan() {
        try {
            String subject = subjectField.getText().trim();
            String hours = hoursField.getText().trim();
            LocalDate date = targetDatePicker.getValue();
            String priority = priorityComboBox.getValue();
            int studied = parseIntOrZero(studiedHoursField.getText());
            
            if (subject.isEmpty()) {
                showAlert("입력 오류", "과목명을 입력해주세요!");
                return;
            }
            
            int target = parseIntOrZero(hours.isEmpty() ? "0" : hours);
            int total = Math.min(studied, target);
            
            StudyPlanItem newItem = new StudyPlanItem(
                subject,
                String.valueOf(target),
                date != null ? date.toString() : null,
                priority,
                total + "h / " + target + "h"
            );
            
            planTable.getItems().add(newItem);
            savePlans();
            updateStatusLabel();
            clearForm();
            showAlert("성공", "새 계획이 추가되었습니다!");
            
        } catch (Exception ex) {
            showAlert("오류", "계획 추가 중 오류가 발생했습니다: " + ex.getMessage());
        }
    }
    
    private void clearForm() {
        subjectField.clear();
        hoursField.clear();
        targetDatePicker.setValue(LocalDate.now().plusWeeks(1));
        priorityComboBox.setValue("보통");
        studiedHoursField.clear();
        planTable.getSelectionModel().clearSelection();
    }
    
    private void updateStatusLabel() {
        int totalPlans = planTable.getItems().size();
        int completedPlans = 0;
        
        for (StudyPlanItem item : planTable.getItems()) {
            String status = item.getStatus();
            if (status != null && status.contains("/")) {
                String[] parts = status.split("/");
                try {
                    int studied = parseIntOrZero(parts[0].trim().replace("h", ""));
                    int target = parseIntOrZero(parts[1].trim().replace("h", ""));
                    if (studied >= target && target > 0) {
                        completedPlans++;
                    }
                } catch (Exception e) {
                    // 파싱 오류 무시
                }
            }
        }
        
        double progressRate = totalPlans > 0 ? (double) completedPlans / totalPlans * 100 : 0;
        
        statusLabel.setText(String.format(
            "📊 총 계획: %d개 | 완료: %d개 | 진행률: %.1f%%",
            totalPlans, completedPlans, progressRate
        ));
    }

    /**
     * 데이터 새로고침 (외부에서 호출 가능)
     */
    public void refreshData() {
        loadPlans();
        updateStatusLabel();
        System.out.println("🔄 공부 계획 데이터 새로고침 완료");
    }

    private void loadPlans() {
        Map<String,StudyPlanItem> planMap = new HashMap<>();
        File file = new File(DATA_DIR + PLAN_FILE);
        
        // 기존 파일에서 계획 데이터 로드
        if (file.exists()) {
            try (BufferedReader r = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = r.readLine()) != null) {
                    String[] p = line.split("\\|", -1);
                    if (p.length >= 5) {
                        String subj     = p[0].isEmpty() ? null : p[0];
                        String hours    = p[1].isEmpty() ? "0"  : p[1];
                        String date     = p[2].isEmpty() ? null : p[2];
                        String priority = p[3].isEmpty() ? "보통" : p[3];
                        String status   = p[4].isEmpty() ? "0h / 0h": p[4];
                        
                        if (subj != null) {
                            planMap.put(subj, new StudyPlanItem(subj, hours, date, priority, status));
                        }
                    }
                }
            } catch (IOException ex) {
                System.err.println("❌ loadPlans 실패: " + ex.getMessage());
            }
        }

        // 현재 시간표의 모든 과목과 기존 계획을 병합
        List<StudyPlanItem> merged = new ArrayList<>();
        
        // 1. 시간표에 있는 과목들 추가
        for (Subject s : dbManager.getAllSubjects()) {
            String name = s.getName();
            if (planMap.containsKey(name)) {
                // 기존 계획이 있으면 그것을 사용
                merged.add(planMap.get(name));
                planMap.remove(name); // 처리된 것은 제거
            } else {
                // 새로운 과목이면 기본 계획 생성
                merged.add(new StudyPlanItem(name, "0", null, "보통", "0h / 0h"));
            }
        }
        
        // 2. 시간표에는 없지만 기존 계획에 있는 과목들도 유지
        merged.addAll(planMap.values());
        
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
                    it.getPriority() == null ? "보통" : it.getPriority(),
                    it.getStatus()   == null ? "0h / 0h": it.getStatus()
                ));
            }
            System.out.println("💾 공부 계획 저장 완료");
        } catch (IOException ex) {
            System.err.println("❌ savePlans 실패: " + ex.getMessage());
        }
    }

    private int parseIntOrZero(String s) {
        try { 
            return Integer.parseInt(s.trim().replace("h", ""));
        } catch (Exception e) { 
            return 0; 
        }
    }

    private int extractStudiedHours(String status) {
        if (status == null || !status.contains("/")) return 0;
        return parseIntOrZero(status.split("/", 2)[0]);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ===== DataChangeListener 인터페이스 구현 =====
    
    @Override
    public void onSubjectChanged(String changeType, int subjectId) {
        // UI 업데이트는 JavaFX Application Thread에서 실행해야 함
        Platform.runLater(() -> {
            System.out.println("🔄 StudyPlanPanel: 과목 변경 감지 (" + changeType + ")");
            refreshData();
        });
    }

    /**
     * 패널이 닫힐 때 리스너 해제
     */
    public void dispose() {
        dbManager.removeDataChangeListener(this);
        System.out.println("🔗 StudyPlanPanel 리스너 해제");
    }

    // ===== 데이터 모델 클래스 =====
    
    public static class StudyPlanItem {
        private String subject, hours, date, priority, status;
        
        public StudyPlanItem(String s, String h, String d, String p, String st) {
            subject  = s; 
            hours    = h; 
            date     = d; 
            priority = p; 
            status   = st;
        }
        
        public String getSubject()  { return subject; }
        public String getHours()    { return hours;   }
        public String getDate()     { return date;    }
        public String getPriority() { return priority;}
        public String getStatus()   { return status;  }
        
        public void setHours(String h)    { hours    = h;  }
        public void setDate(String d)     { date     = d;  }
        public void setPriority(String p) { priority = p;  }
        public void setStatus(String st)  { status   = st; }
    }
}