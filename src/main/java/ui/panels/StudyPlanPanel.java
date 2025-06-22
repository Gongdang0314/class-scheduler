package ui.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;                       // ← 추가: HBox 정렬을 위해
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;    // ← 추가: GridPane 컬럼 너비 조절
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;             // ← 추가: HBox 성장 비율 지정
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

/**
 * 공부계획 탭의 UI 패널
 * ───────────────────────────────────────────────────────────
 * 왼쪽: 계획 추가/수정/삭제/저장/불러오기
 * 오른쪽: 공부 시간 반영
 * 하단: 현재 계획 목록 테이블
 */
public class StudyPlanPanel extends VBox {
    
    // ─── 왼쪽: 계획 관리 폼 ────────────────────────────────────
    private TableView<StudyPlanItem> planTable;
    private TextField subjectField;
    private TextField hoursField;
    private DatePicker targetDatePicker;
    private ComboBox<String> priorityComboBox;
    
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button saveButton;    // 저장 버튼
    private Button loadButton;    // 불러오기 버튼
    
    // ─── 오른쪽: 공부 시간 반영 폼 ───────────────────────────────
    private ComboBox<String> subjectSelectComboBox;
    private TextField studiedHoursField;
    private Button reflectButton;
    
    public StudyPlanPanel() {
        super(UIStyleManager.STANDARD_SPACING);
        initializeComponents();  // 컴포넌트 생성 & 이벤트 바인딩
        setupLayout();           // 레이아웃 배치
        applyStyles();           // CSS 스타일 적용
    }
    
    /** 컴포넌트 생성 & 이벤트 핸들러 설정 */
    private void initializeComponents() {
        // ← 왼쪽 폼: 과목/목표시간/목표일/우선순위 입력
        subjectField     = UIStyleManager.createStandardTextField("과목명");
        hoursField       = UIStyleManager.createStandardTextField("목표 시간");
        targetDatePicker = UIStyleManager.createStandardDatePicker();
        priorityComboBox = UIStyleManager.createStandardComboBox();
        priorityComboBox.getItems().addAll("높음", "보통", "낮음");
        priorityComboBox.setValue("보통");
        
        addButton    = UIStyleManager.createPrimaryButton("계획 추가");
        editButton   = UIStyleManager.createSecondaryButton("수정");
        deleteButton = UIStyleManager.createSecondaryButton("삭제");
        saveButton   = UIStyleManager.createSecondaryButton("저장");
        loadButton   = UIStyleManager.createSecondaryButton("불러오기");
        
        UIStyleManager.applyTooltip(addButton,    "새로운 공부 계획을 추가합니다");
        UIStyleManager.applyTooltip(editButton,   "선택된 계획을 수정합니다");
        UIStyleManager.applyTooltip(deleteButton, "선택된 계획을 삭제합니다");
        UIStyleManager.applyTooltip(saveButton,   "현재 계획을 파일로 저장합니다");
        UIStyleManager.applyTooltip(loadButton,   "저장된 계획을 불러옵니다");
        
        planTable = UIStyleManager.createStandardTableView();
        setupTableColumns();  // 컬럼과 데이터 모델 매핑
        
        // ← 오른쪽 폼: 과목 선택 + 공부한 시간 입력
        subjectSelectComboBox = UIStyleManager.createStandardComboBox();
        subjectSelectComboBox.setPromptText("선택하세요");
        subjectSelectComboBox.setPrefWidth(150);
        
        studiedHoursField = UIStyleManager.createStandardTextField("공부한 시간");
        // PrefWidth 조정해도 label이 잘리지 않도록 GridPane 컬럼 제약 조건도 설정
        studiedHoursField.setPrefWidth(150);
        
        reflectButton = UIStyleManager.createPrimaryButton("반영하기");
        UIStyleManager.applyTooltip(reflectButton, "입력한 시간을 계획에 반영합니다");
        
        // “계획 추가” 버튼 클릭 시: 테이블 + 콤보박스에 과목 추가, 초기 상태 0%로 설정
        addButton.setOnAction(e -> {
            String subject = subjectField.getText();
            String hours   = hoursField.getText();
            String date    = targetDatePicker.getValue() != null
                                 ? targetDatePicker.getValue().toString()
                                 : "";
            String priority = priorityComboBox.getValue();
            
            int targetH;
            try {
                targetH = Integer.parseInt(hours);
            } catch (Exception ex) {
                targetH = 0;
            }
            // 초기 상태: 0/목표H (0%😭)
            String initStatus = String.format("0/%dH (0%%%s)", targetH, "😭");
            
            StudyPlanItem item = new StudyPlanItem(subject, hours, date, priority, initStatus);
            planTable.getItems().add(item);
            subjectSelectComboBox.getItems().add(subject);
            
            subjectField.clear();
            hoursField.clear();
            targetDatePicker.setValue(null);
            priorityComboBox.setValue("보통");
        });
        
        // “반영하기” 버튼 클릭 시: 상태 컬럼에 진도 표시
        reflectButton.setOnAction(e -> {
            String selSubject = subjectSelectComboBox.getValue();
            if (selSubject == null) return;
            
            int studied;
            try {
                studied = Integer.parseInt(studiedHoursField.getText());
            } catch (Exception ex) {
                studied = 0;
            }
            
            for (StudyPlanItem item : planTable.getItems()) {
                if (selSubject.equals(item.getSubject())) {
                    int target;
                    try {
                        target = Integer.parseInt(item.getHours());
                    } catch (Exception ex) {
                        target = 0;
                    }
                    int percent = (target > 0)
                        ? Math.min(100, Math.round(studied * 100f / target))
                        : 0;
                    
                    String emoji;
                    if      (percent >= 100) emoji = "🤩";
                    else if (percent >=  71) emoji = "😎";
                    else if (percent >=  31) emoji = "😌";
                    else                      emoji = "😭";
                    
                    String newStatus = String.format(
                        "%d/%dH (%d%%%s)", studied, target, percent, emoji
                    );
                    item.setStatus(newStatus);
                    planTable.refresh();
                    break;
                }
            }
            studiedHoursField.clear();
        });
        
        // “저장”/“불러오기” 버튼 (왼쪽 플랜 저장/불러오기)
        saveButton.setOnAction(e -> {
            System.out.println("▶ 계획 저장: 구현 필요");
        });
        loadButton.setOnAction(e -> {
            System.out.println("▶ 계획 불러오기: 구현 필요");
        });
    }
    
    /** TableView 컬럼 정의 및 PropertyValueFactory 매핑 */
    private void setupTableColumns() {
        TableColumn<StudyPlanItem, String> subjectCol  = new TableColumn<>("과목");
        TableColumn<StudyPlanItem, String> hoursCol    = new TableColumn<>("목표시간");
        TableColumn<StudyPlanItem, String> dateCol     = new TableColumn<>("목표일");
        TableColumn<StudyPlanItem, String> priorityCol = new TableColumn<>("우선순위");
        TableColumn<StudyPlanItem, String> statusCol   = new TableColumn<>("진행상태");
        
        subjectCol .setCellValueFactory(new PropertyValueFactory<>("subject"));
        hoursCol   .setCellValueFactory(new PropertyValueFactory<>("hours"));
        dateCol    .setCellValueFactory(new PropertyValueFactory<>("date"));
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        statusCol  .setCellValueFactory(new PropertyValueFactory<>("status"));
        
        subjectCol .setPrefWidth(120);
        hoursCol   .setPrefWidth(80);
        dateCol    .setPrefWidth(100);
        priorityCol.setPrefWidth(80);
        statusCol  .setPrefWidth(150);
        
        planTable.getColumns().addAll(subjectCol, hoursCol, dateCol, priorityCol, statusCol);
        planTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
    
    /** 폼(좌/우)과 테이블을 화면에 배치 */
    private void setupLayout() {
        Label titleLabel = UIStyleManager.createTitleLabel("📚 공부 계획 관리");
        
        // ← 왼쪽 컨테이너
        GridPane leftForm = UIStyleManager.createFormGrid();
        leftForm.add(new Label("과목:"),       0, 0);
        leftForm.add(subjectField,             1, 0);
        leftForm.add(new Label("목표시간:"),   0, 1);
        leftForm.add(hoursField,               1, 1);
        leftForm.add(new Label("목표일:"),      0, 2);
        leftForm.add(targetDatePicker,         1, 2);
        leftForm.add(new Label("우선순위:"),    0, 3);
        leftForm.add(priorityComboBox,         1, 3);
        
        HBox leftButtons = UIStyleManager.createStandardHBox();
        leftButtons.setSpacing(10);
        leftButtons.getChildren().addAll(
            addButton, editButton, deleteButton, saveButton, loadButton
        );
        
        VBox leftContainer = UIStyleManager.createStandardContainer();
        leftContainer.getChildren().addAll(leftForm, leftButtons);
        // 왼쪽 컨테이너가 HBox 안에서 동일 비율로 늘어나도록 설정
        HBox.setHgrow(leftContainer, Priority.ALWAYS);
        leftContainer.setMaxWidth(Double.MAX_VALUE);
        
        // → 오른쪽 컨테이너
        GridPane rightForm = UIStyleManager.createFormGrid();
        rightForm.setHgap(20);
        rightForm.setVgap(10);
        // 첫 번째 컬럼 최소 너비 지정: "공부한 시간" 레이블이 잘리지 않도록
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(100);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        rightForm.getColumnConstraints().addAll(col1, col2);
        
        rightForm.add(new Label("과목:"),        0, 0);
        rightForm.add(subjectSelectComboBox,    1, 0);
        rightForm.add(new Label("공부한 시간:"), 0, 1);
        rightForm.add(studiedHoursField,        1, 1);
        
        VBox rightContainer = UIStyleManager.createStandardContainer();
        rightContainer.getChildren().addAll(
            UIStyleManager.createSubLabel("⏱ 공부 시간 반영"),
            rightForm,
            reflectButton
        );
        // 오른쪽 컨테이너도 동일 비율로 늘어나도록
        HBox.setHgrow(rightContainer, Priority.ALWAYS);
        rightContainer.setMaxWidth(Double.MAX_VALUE);
        
        // 상단 HBox: 좌우 컨테이너 1:1 비율, 중앙 정렬
        HBox topBox = UIStyleManager.createStandardHBox();
        topBox.setSpacing(30);
        topBox.setAlignment(Pos.CENTER);
        topBox.getChildren().addAll(leftContainer, rightContainer);
        
        // 하단: 현재 계획 목록 테이블
        VBox tableContainer = UIStyleManager.createStandardContainer();
        tableContainer.getChildren().addAll(
            UIStyleManager.createSubLabel("현재 계획 목록"),
            planTable
        );
        
        this.getChildren().addAll(titleLabel, topBox, tableContainer);
    }
    
    /** 전체 패널 여백 및 스타일 적용 */
    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
    
    /** TableView 데이터 모델 */
    public static class StudyPlanItem {
        private String subject;
        private String hours;
        private String date;
        private String priority;
        private String status;
        
        public StudyPlanItem(String subject, String hours, String date,
                             String priority, String status) {
            this.subject  = subject;
            this.hours    = hours;
            this.date     = date;
            this.priority = priority;
            this.status   = status;
        }
        
        public String getSubject()  { return subject; }
        public String getHours()    { return hours; }
        public String getDate()     { return date; }
        public String getPriority() { return priority; }
        public String getStatus()   { return status; }
        
        public void setStatus(String status) {
            this.status = status;
        }
    }
}
