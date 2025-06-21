package ui.panels;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

/**
 * 과제/시험 관리 탭의 UI 패널
 */
public class AssignmentExamPanel extends VBox {
    
    private TableView<TaskItem> taskTable;
    private TableView<TaskItem> urgentTable;
    private TextField titleField;
    private TextField subjectField;
    private DatePicker dueDatePicker;
    private ComboBox<String> typeComboBox;
    private ComboBox<String> priorityComboBox;
    private TextArea descriptionArea;
    private Label statsLabel;
    
    public AssignmentExamPanel() {
        super(UIStyleManager.STANDARD_SPACING);
        initializeComponents();
        setupLayout();
        applyStyles();
    }
    
    private void initializeComponents() {
        // 입력 폼 컴포넌트
        titleField = UIStyleManager.createStandardTextField("제목");
        subjectField = UIStyleManager.createStandardTextField("과목명");
        dueDatePicker = UIStyleManager.createStandardDatePicker();
        
        typeComboBox = UIStyleManager.createStandardComboBox();
        typeComboBox.getItems().addAll("과제", "시험", "프로젝트", "발표", "퀴즈");
        typeComboBox.setValue("과제");
        
        priorityComboBox = UIStyleManager.createStandardComboBox();
        priorityComboBox.getItems().addAll("매우높음", "높음", "보통", "낮음");
        priorityComboBox.setValue("보통");
        
        descriptionArea = new TextArea();
        descriptionArea.setPromptText("상세 설명을 입력하세요...");
        descriptionArea.setPrefRowCount(3);
        descriptionArea.getStyleClass().add("text-field");
        
        // 통계 라벨
        statsLabel = UIStyleManager.createSubLabel("📊 전체: 0건 | 완료: 0건 | 미완료: 0건");
        
        // 테이블 설정
        taskTable = UIStyleManager.createStandardTableView();
        setupTaskTable();
        
        urgentTable = UIStyleManager.createStandardTableView();
        setupUrgentTable();
    }
    
    private void setupTaskTable() {
        TableColumn<TaskItem, String> titleCol = new TableColumn<>("제목");
        TableColumn<TaskItem, String> subjectCol = new TableColumn<>("과목");
        TableColumn<TaskItem, String> typeCol = new TableColumn<>("유형");
        TableColumn<TaskItem, String> dueDateCol = new TableColumn<>("마감일");
        TableColumn<TaskItem, String> priorityCol = new TableColumn<>("우선순위");
        TableColumn<TaskItem, String> statusCol = new TableColumn<>("상태");
        TableColumn<TaskItem, String> daysLeftCol = new TableColumn<>("남은일수");
        
        titleCol.setPrefWidth(150);
        subjectCol.setPrefWidth(100);
        typeCol.setPrefWidth(70);
        dueDateCol.setPrefWidth(100);
        priorityCol.setPrefWidth(80);
        statusCol.setPrefWidth(80);
        daysLeftCol.setPrefWidth(80);
        
        taskTable.getColumns().addAll(titleCol, subjectCol, typeCol, dueDateCol, priorityCol, statusCol, daysLeftCol);
    }
    
    private void setupUrgentTable() {
        TableColumn<TaskItem, String> titleCol = new TableColumn<>("제목");
        TableColumn<TaskItem, String> subjectCol = new TableColumn<>("과목");
        TableColumn<TaskItem, String> dueDateCol = new TableColumn<>("마감일");
        TableColumn<TaskItem, String> daysLeftCol = new TableColumn<>("남은일수");
        
        titleCol.setPrefWidth(180);
        subjectCol.setPrefWidth(100);
        dueDateCol.setPrefWidth(100);
        daysLeftCol.setPrefWidth(80);
        
        urgentTable.getColumns().addAll(titleCol, subjectCol, dueDateCol, daysLeftCol);
        urgentTable.setPrefHeight(150);
    }
    
    private void setupLayout() {
        // 제목
        Label titleLabel = UIStyleManager.createTitleLabel("📝 과제 & 시험 관리");
        
        // 상단: 긴급 일정 알림
        VBox urgentContainer = UIStyleManager.createStandardContainer();
        urgentContainer.setStyle("-fx-background-color: #FFF3CD; -fx-border-color: #FFEAA7;");
        Label urgentTitle = UIStyleManager.createTitleLabel("⚠️ 마감 임박 (7일 이내)");
        urgentTitle.setStyle("-fx-text-fill: #856404;");
        urgentContainer.getChildren().addAll(urgentTitle, urgentTable);
        
        // 메인 컨텐츠를 좌우로 분할
        HBox mainContent = new HBox(UIStyleManager.STANDARD_SPACING);
        
        // 왼쪽: 입력 폼
        VBox leftPane = new VBox(UIStyleManager.STANDARD_SPACING);
        leftPane.setPrefWidth(400);
        
        VBox formContainer = UIStyleManager.createStandardContainer();
        Label formTitle = UIStyleManager.createSubLabel("➕ 새 항목 추가");
        
        GridPane formGrid = UIStyleManager.createFormGrid();
        formGrid.add(new Label("제목:"), 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(new Label("과목:"), 0, 1);
        formGrid.add(subjectField, 1, 1);
        formGrid.add(new Label("유형:"), 0, 2);
        formGrid.add(typeComboBox, 1, 2);
        formGrid.add(new Label("마감일:"), 0, 3);
        formGrid.add(dueDatePicker, 1, 3);
        formGrid.add(new Label("우선순위:"), 0, 4);
        formGrid.add(priorityComboBox, 1, 4);
        formGrid.add(new Label("설명:"), 0, 5);
        formGrid.add(descriptionArea, 1, 5);
        
        // 버튼 영역
        HBox buttonBox = UIStyleManager.createStandardHBox();
        Button addButton = UIStyleManager.createPrimaryButton("추가");
        Button editButton = UIStyleManager.createSecondaryButton("수정");
        Button deleteButton = UIStyleManager.createSecondaryButton("삭제");
        Button completeButton = UIStyleManager.createPrimaryButton("완료 처리");
        
        UIStyleManager.applyTooltip(addButton, "새로운 과제나 시험을 추가합니다");
        UIStyleManager.applyTooltip(completeButton, "선택된 항목을 완료 처리합니다");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, completeButton);
        
        formContainer.getChildren().addAll(formTitle, formGrid, buttonBox);
        
        // 필터 및 정렬 옵션
        VBox filterContainer = UIStyleManager.createStandardContainer();
        Label filterTitle = UIStyleManager.createSubLabel("🔍 필터 & 정렬");
        
        HBox filterBox = UIStyleManager.createStandardHBox();
        ComboBox<String> filterTypeBox = UIStyleManager.createStandardComboBox();
        filterTypeBox.getItems().addAll("전체", "과제", "시험", "프로젝트", "발표", "퀴즈");
        filterTypeBox.setValue("전체");
        
        ComboBox<String> filterStatusBox = UIStyleManager.createStandardComboBox();
        filterStatusBox.getItems().addAll("전체", "진행중", "완료", "지연");
        filterStatusBox.setValue("전체");
        
        ComboBox<String> sortBox = UIStyleManager.createStandardComboBox();
        sortBox.getItems().addAll("마감일순", "추가일순", "우선순위순", "과목순");
        sortBox.setValue("마감일순");
        
        filterBox.getChildren().addAll(
            new Label("유형:"), filterTypeBox,
            new Label("상태:"), filterStatusBox,
            new Label("정렬:"), sortBox
        );
        
        filterContainer.getChildren().addAll(filterTitle, filterBox);
        
        leftPane.getChildren().addAll(formContainer, filterContainer);
        
        // 오른쪽: 과제/시험 목록
        VBox rightPane = new VBox(UIStyleManager.STANDARD_SPACING);
        rightPane.setPrefWidth(600);
        
        VBox tableContainer = UIStyleManager.createStandardContainer();
        Label tableTitle = UIStyleManager.createSubLabel("📋 전체 목록");
        
        // 통계 정보
        HBox statsBox = UIStyleManager.createStandardHBox();
        statsBox.getChildren().add(statsLabel);
        
        tableContainer.getChildren().addAll(tableTitle, statsBox, taskTable);
        
        // 진행률 표시 (선택사항)
        VBox progressContainer = UIStyleManager.createStandardContainer();
        Label progressTitle = UIStyleManager.createSubLabel("📈 진행 현황");
        
        ProgressBar overallProgress = new ProgressBar(0.0);
        overallProgress.setPrefWidth(200);
        Label progressLabel = new Label("전체 진행률: 0%");
        progressLabel.getStyleClass().add("label-sub");
        
        HBox progressBox = UIStyleManager.createStandardHBox();
        progressBox.getChildren().addAll(overallProgress, progressLabel);
        
        progressContainer.getChildren().addAll(progressTitle, progressBox);
        
        rightPane.getChildren().addAll(tableContainer, progressContainer);
        
        // 전체 레이아웃
        mainContent.getChildren().addAll(leftPane, rightPane);
        this.getChildren().addAll(titleLabel, urgentContainer, mainContent);
    }
    
    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
    
    // 더미 데이터 클래스
    public static class TaskItem {
        private String title;
        private String subject;
        private String type;
        private String dueDate;
        private String priority;
        private String status;
        private String daysLeft;
        private String description;
        
        public TaskItem(String title, String subject, String type, String dueDate, 
                       String priority, String status, String daysLeft, String description) {
            this.title = title;
            this.subject = subject;
            this.type = type;
            this.dueDate = dueDate;
            this.priority = priority;
            this.status = status;
            this.daysLeft = daysLeft;
            this.description = description;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public String getSubject() { return subject; }
        public String getType() { return type; }
        public String getDueDate() { return dueDate; }
        public String getPriority() { return priority; }
        public String getStatus() { return status; }
        public String getDaysLeft() { return daysLeft; }
        public String getDescription() { return description; }
    }
}