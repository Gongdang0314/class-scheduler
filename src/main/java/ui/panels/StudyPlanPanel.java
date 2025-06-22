package ui.panels;

import java.time.LocalDate;
import java.util.Comparator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

/**
 * 공부계획 탭의 UI 패널
 */
public class StudyPlanPanel extends VBox {

    // ─── 필드 선언 ───────────────────────────────────────────
    private TableView<StudyPlanItem> planTable;
    private TextField subjectField, hoursField;
    private DatePicker targetDatePicker;
    private ComboBox<String> priorityComboBox;

    private Button addButton, editButton, deleteButton, saveButton, loadButton;

    private ComboBox<String> subjectSelectComboBox;
    private TextField studiedHoursField;
    private Button reflectButton;

    // ─── 생성자 ─────────────────────────────────────────────
    public StudyPlanPanel() {
        initializeFields();
        initButtons();
        initTable();
        initReflectForm();
        layoutComponents();
        hookHandlers();
        applyStyles();
    }

    // ─── 컴포넌트 생성 ───────────────────────────────────────
    private void initializeFields() {
        subjectField     = UIStyleManager.createStandardTextField("과목명");
        hoursField       = UIStyleManager.createStandardTextField("목표 시간");
        targetDatePicker = UIStyleManager.createStandardDatePicker();
        priorityComboBox = UIStyleManager.createStandardComboBox();
        priorityComboBox.getItems().addAll("높음", "보통", "낮음");
        priorityComboBox.setValue("보통");

        planTable = UIStyleManager.createStandardTableView();
    }

    // ─── 버튼 초기화 ─────────────────────────────────────────
    private void initButtons() {
        addButton     = UIStyleManager.createPrimaryButton("계획 추가");
        editButton    = UIStyleManager.createSecondaryButton("수정");
        deleteButton  = UIStyleManager.createSecondaryButton("삭제");
        saveButton    = UIStyleManager.createSecondaryButton("저장");
        loadButton    = UIStyleManager.createSecondaryButton("불러오기");
        reflectButton = UIStyleManager.createPrimaryButton("반영하기");

        UIStyleManager.applyTooltip(addButton,     "새로운 계획 추가");
        UIStyleManager.applyTooltip(editButton,    "선택된 계획 수정");
        UIStyleManager.applyTooltip(deleteButton,  "선택된 계획 삭제");
        UIStyleManager.applyTooltip(saveButton,    "계획 저장");
        UIStyleManager.applyTooltip(loadButton,    "계획 불러오기");
        UIStyleManager.applyTooltip(reflectButton, "공부 시간 반영");
    }

    // ─── 테이블 컬럼 설정 ────────────────────────────────────
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

        c1.setPrefWidth(120);
        c2.setPrefWidth(80);
        c3.setPrefWidth(100);
        c4.setPrefWidth(80);
        c5.setPrefWidth(150);

        planTable.getColumns().addAll(c1,c2,c3,c4,c5);
        planTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    // ─── 반영 폼 생성 ─────────────────────────────────────────
    private void initReflectForm() {
        subjectSelectComboBox = UIStyleManager.createStandardComboBox();
        subjectSelectComboBox.setPromptText("선택하세요");
        subjectSelectComboBox.setPrefWidth(150);

        studiedHoursField = UIStyleManager.createStandardTextField("공부한 시간");
        studiedHoursField.setPrefWidth(150);
    }

    // ─── 레이아웃 배치 ───────────────────────────────────────
    private void layoutComponents() {
        Label title = UIStyleManager.createTitleLabel("📚 공부 계획 관리");

        // 왼쪽 입력 폼
        GridPane leftForm = UIStyleManager.createFormGrid();
        leftForm.add(new Label("과목:"),     0,0);
        leftForm.add(subjectField,           1,0);
        leftForm.add(new Label("목표시간:"), 0,1);
        leftForm.add(hoursField,             1,1);
        leftForm.add(new Label("목표일:"),   0,2);
        leftForm.add(targetDatePicker,       1,2);
        leftForm.add(new Label("우선순위:"), 0,3);
        leftForm.add(priorityComboBox,       1,3);

        HBox leftBtns = UIStyleManager.createStandardHBox();
        leftBtns.setSpacing(10);
        leftBtns.getChildren().addAll(addButton, editButton, deleteButton, saveButton, loadButton);

        VBox leftBox = UIStyleManager.createStandardContainer();
        leftBox.getChildren().addAll(leftForm, leftBtns);
        HBox.setHgrow(leftBox, Priority.ALWAYS);
        leftBox.setMaxWidth(Double.MAX_VALUE);

        // 오른쪽 반영 폼
        GridPane rightForm = UIStyleManager.createFormGrid();
        rightForm.setHgap(20);
        rightForm.setVgap(10);
        ColumnConstraints colA = new ColumnConstraints(); colA.setMinWidth(100);
        ColumnConstraints colB = new ColumnConstraints(); colB.setHgrow(Priority.ALWAYS);
        rightForm.getColumnConstraints().addAll(colA,colB);
        rightForm.add(new Label("과목:"),        0,0);
        rightForm.add(subjectSelectComboBox,    1,0);
        rightForm.add(new Label("공부한 시간:"),0,1);
        rightForm.add(studiedHoursField,        1,1);

        VBox rightBox = UIStyleManager.createStandardContainer();
        rightBox.getChildren().addAll(
            UIStyleManager.createSubLabel("⏱ 공부 시간 반영"),
            rightForm, reflectButton
        );
        HBox.setHgrow(rightBox, Priority.ALWAYS);
        rightBox.setMaxWidth(Double.MAX_VALUE);

        // 좌우 1:1 중앙 배치
        HBox top = UIStyleManager.createStandardHBox();
        top.setSpacing(30);
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(leftBox, rightBox);

        // 하단 테이블
        VBox tableBox = UIStyleManager.createStandardContainer();
        tableBox.getChildren().addAll(
            UIStyleManager.createSubLabel("현재 계획 목록"),
            planTable
        );

        this.getChildren().addAll(title, top, tableBox);
    }

    // ─── 이벤트 핸들러 연결 ─────────────────────────────────
    private void hookHandlers() {
        planTable.setOnMouseClicked((MouseEvent e) -> loadFormFromSelection());
        addButton    .setOnAction(e -> { addItem(); sortItems(); });
        editButton   .setOnAction(e -> { editItem(); sortItems(); });
        deleteButton .setOnAction(e -> { deleteItem(); sortItems(); });
        reflectButton.setOnAction(e -> reflectTime());
        saveButton   .setOnAction(e -> {/* 저장 로직 구현 */});
        loadButton   .setOnAction(e -> {/* 불러오기 로직 구현 */});
    }

    // ─── 추가 로직 ─────────────────────────────────────────
    private void addItem() {
        String sub  = subjectField.getText();
        String hrs  = hoursField.getText();
        String dt   = (targetDatePicker.getValue() != null)
                      ? targetDatePicker.getValue().toString() : "";
        String pri  = priorityComboBox.getValue();
        int targetH = parseIntOrZero(hrs);
        String init = String.format("0/%dH (0%%%s)", targetH, " .·´¯`(>▂<)´¯`·. ");

        StudyPlanItem it = new StudyPlanItem(sub, hrs, dt, pri, init);
        planTable.getItems().add(it);
        subjectSelectComboBox.getItems().add(sub);

        clearForm();
    }

    // ─── 수정 로직 ─────────────────────────────────────────
    private void editItem() {
        String subj = subjectField.getText();
        for (StudyPlanItem it : planTable.getItems()) {
            if (it.getSubject().equals(subj)) {
                // 1) 기존 공부시간 파싱
                int studied = extractStudiedHours(it.getStatus());

                // 2) 필드 업데이트
                it.setSubject(subjectField.getText());
                it.setHours(hoursField.getText());
                it.setDate((targetDatePicker.getValue() != null)
                           ? targetDatePicker.getValue().toString() : "");
                it.setPriority(priorityComboBox.getValue());

                // 3) 새로운 진행 상태 ASCII 이모티콘으로 계산
                int targetH = parseIntOrZero(it.getHours());
                int percent = (targetH > 0)
                    ? Math.min(100, Math.round(studied * 100f / targetH))
                    : 0;
                String icon;
                if      (percent >= 100) icon = " q(≧▽≦q) ";
                else if (percent >=  71) icon = " ✪ ω ✪ ";
                else if (percent >=  31) icon = " (╹ڡ╹ ) ";
                else                      icon = " .·´¯`(>▂<)´¯`·. ";

                it.setStatus(String.format("%d/%dH (%d%%%s)", studied, targetH, percent, icon));
                break;
            }
        }
        clearForm();
    }

    // ─── 삭제 로직 ─────────────────────────────────────────
    private void deleteItem() {
        String subj = subjectField.getText();
        planTable.getItems().removeIf(it -> it.getSubject().equals(subj));
        subjectSelectComboBox.getItems().remove(subj);
        clearForm();
    }

    // ─── 공부시간 반영 로직 ────────────────────────────────────
    private void reflectTime() {
        String sub = subjectSelectComboBox.getValue();
        if (sub == null) return;
        int studied = parseIntOrZero(studiedHoursField.getText());
        for (StudyPlanItem it : planTable.getItems()) {
            if (it.getSubject().equals(sub)) {
                int tgt = parseIntOrZero(it.getHours());
                int pct = (tgt > 0) ? Math.min(100, Math.round(studied * 100f / tgt)) : 0;
                String icon;
                if      (pct >= 100) icon = " q(≧▽≦q) ";
                else if (pct >=  71) icon = " ✪ ω ✪ ";
                else if (pct >=  31) icon = " (╹ڡ╹ ) ";
                else                  icon = " .·´¯`(>▂<)´¯`·. ";
                it.setStatus(String.format("%d/%dH (%d%%%s)", studied, tgt, pct, icon));
                break;
            }
        }
        planTable.refresh();
        studiedHoursField.clear();
    }

    // ─── 정렬 로직 ─────────────────────────────────────────
    private void sortItems() {
        planTable.getItems().sort(Comparator
            .comparingInt((StudyPlanItem it) -> priorityRank(it.getPriority()))
            .thenComparing(StudyPlanItem::getSubject));
    }
    private int priorityRank(String p) {
        switch (p) {
            case "높음": return 0;
            case "보통": return 1;
            case "낮음": return 2;
            default:     return 3;
        }
    }

    // ─── 폼 로드 & 클리어 ───────────────────────────────────
    private void loadFormFromSelection() {
        StudyPlanItem sel = planTable.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        subjectField.setText(sel.getSubject());
        hoursField  .setText(sel.getHours());
        if (!sel.getDate().isEmpty()) {
            targetDatePicker.setValue(LocalDate.parse(sel.getDate()));
        } else {
            targetDatePicker.setValue(null);
        }
        priorityComboBox.setValue(sel.getPriority());
    }
    private void clearForm() {
        planTable.getSelectionModel().clearSelection();
        subjectField.clear();
        hoursField.clear();
        targetDatePicker.setValue(null);
        priorityComboBox.setValue("보통");
    }

    // ─── 문자열 파싱 유틸 ─────────────────────────────────────
    private int parseIntOrZero(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
    private int extractStudiedHours(String status) {
        try {
            return Integer.parseInt(status.substring(0, status.indexOf("/")));
        } catch (Exception e) {
            return 0;
        }
    }

    // ─── 스타일 적용 ─────────────────────────────────────────
    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }

    // ─── 데이터 모델 ─────────────────────────────────────────
    public static class StudyPlanItem {
        private String subject, hours, date, priority, status;
        public StudyPlanItem(String s, String h, String d, String p, String st) {
            subject = s; hours = h; date = d; priority = p; status = st;
        }
        public String getSubject()  { return subject;  }
        public String getHours()    { return hours;    }
        public String getDate()     { return date;     }
        public String getPriority() { return priority; }
        public String getStatus()   { return status;   }
        public void setSubject(String s)  { subject = s;  }
        public void setHours(String h)    { hours = h;    }
        public void setDate(String d)     { date = d;     }
        public void setPriority(String p) { priority = p; }
        public void setStatus(String st)  { status = st;  }
    }
}
