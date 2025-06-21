package ui.panels;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

/**
 * 공부계획 탭의 UI 패널
 */
public class StudyPlanPanel extends VBox {
    
    private TableView<StudyPlanItem> planTable;
    private TextField subjectField;
    private TextField hoursField;
    private DatePicker targetDatePicker;
    private ComboBox<String> priorityComboBox;
    
    public StudyPlanPanel() {
        super(UIStyleManager.STANDARD_SPACING);
        initializeComponents();
        setupLayout();
        applyStyles();
    }
    
    private void initializeComponents() {
        // 입력 폼 컴포넌트
        subjectField = UIStyleManager.createStandardTextField("과목명");
        hoursField = UIStyleManager.createStandardTextField("목표 시간");
        targetDatePicker = UIStyleManager.createStandardDatePicker();
        priorityComboBox = UIStyleManager.createStandardComboBox();
        priorityComboBox.getItems().addAll("높음", "보통", "낮음");
        priorityComboBox.setValue("보통");
        
        // 테이블 설정
        planTable = UIStyleManager.createStandardTableView();
        setupTableColumns();
    }
    
    private void setupTableColumns() {
        TableColumn<StudyPlanItem, String> subjectCol = new TableColumn<>("과목");
        TableColumn<StudyPlanItem, String> hoursCol = new TableColumn<>("목표시간");
        TableColumn<StudyPlanItem, String> dateCol = new TableColumn<>("목표일");
        TableColumn<StudyPlanItem, String> priorityCol = new TableColumn<>("우선순위");
        TableColumn<StudyPlanItem, String> statusCol = new TableColumn<>("진행상태");
        
        subjectCol.setPrefWidth(120);
        hoursCol.setPrefWidth(80);
        dateCol.setPrefWidth(100);
        priorityCol.setPrefWidth(80);
        statusCol.setPrefWidth(100);
        
        planTable.getColumns().addAll(subjectCol, hoursCol, dateCol, priorityCol, statusCol);
    }
    
    private void setupLayout() {
        // 제목
        Label titleLabel = UIStyleManager.createTitleLabel("📚 공부 계획 관리");
        
        // 입력 폼
        GridPane formGrid = UIStyleManager.createFormGrid();
        formGrid.add(new Label("과목:"), 0, 0);
        formGrid.add(subjectField, 1, 0);
        formGrid.add(new Label("목표시간:"), 0, 1);
        formGrid.add(hoursField, 1, 1);
        formGrid.add(new Label("목표일:"), 0, 2);
        formGrid.add(targetDatePicker, 1, 2);
        formGrid.add(new Label("우선순위:"), 0, 3);
        formGrid.add(priorityComboBox, 1, 3);
        
        // 버튼 영역
        HBox buttonBox = UIStyleManager.createStandardHBox();
        Button addButton = UIStyleManager.createPrimaryButton("계획 추가");
        Button editButton = UIStyleManager.createSecondaryButton("수정");
        Button deleteButton = UIStyleManager.createSecondaryButton("삭제");
        
        UIStyleManager.applyTooltip(addButton, "새로운 공부 계획을 추가합니다");
        UIStyleManager.applyTooltip(editButton, "선택된 계획을 수정합니다");
        UIStyleManager.applyTooltip(deleteButton, "선택된 계획을 삭제합니다");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);
        
        // 테이블 영역
        VBox tableContainer = UIStyleManager.createStandardContainer();
        Label tableTitle = UIStyleManager.createSubLabel("현재 계획 목록");
        tableContainer.getChildren().addAll(tableTitle, planTable);
        
        // 전체 레이아웃
        VBox formContainer = UIStyleManager.createStandardContainer();
        formContainer.getChildren().addAll(formGrid, buttonBox);
        
        this.getChildren().addAll(titleLabel, formContainer, tableContainer);
    }
    
    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
    
    // 더미 데이터 클래스
    public static class StudyPlanItem {
        private String subject;
        private String hours;
        private String date;
        private String priority;
        private String status;
        
        public StudyPlanItem(String subject, String hours, String date, String priority, String status) {
            this.subject = subject;
            this.hours = hours;
            this.date = date;
            this.priority = priority;
            this.status = status;
        }
        
        // getters and setters
        public String getSubject() { return subject; }
        public String getHours() { return hours; }
        public String getDate() { return date; }
        public String getPriority() { return priority; }
        public String getStatus() { return status; }
    }
}