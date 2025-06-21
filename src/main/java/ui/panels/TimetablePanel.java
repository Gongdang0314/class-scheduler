package ui.panels;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

/**
 * 시간표 탭의 UI 패널
 */
public class TimetablePanel extends VBox {
    
    private GridPane timetableGrid;
    private TextField subjectField;
    private TextField professorField;
    private TextField locationField;
    private ComboBox<String> dayComboBox;
    private ComboBox<String> startTimeComboBox;
    private ComboBox<String> endTimeComboBox;
    private TableView<CourseItem> courseTable;
    
    private static final String[] DAYS = {"월", "화", "수", "목", "금", "토", "일"};
    private static final String[] TIME_SLOTS = {
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00", "19:30", "20:00", "20:30"
    };
    
    public TimetablePanel() {
        super(UIStyleManager.STANDARD_SPACING);
        initializeComponents();
        setupLayout();
        applyStyles();
    }
    
    private void initializeComponents() {
        // 입력 폼 컴포넌트
        subjectField = UIStyleManager.createStandardTextField("과목명");
        professorField = UIStyleManager.createStandardTextField("교수님");
        locationField = UIStyleManager.createStandardTextField("강의실");
        
        dayComboBox = UIStyleManager.createStandardComboBox();
        dayComboBox.getItems().addAll(DAYS);
        
        startTimeComboBox = UIStyleManager.createStandardComboBox();
        startTimeComboBox.getItems().addAll(TIME_SLOTS);
        
        endTimeComboBox = UIStyleManager.createStandardComboBox();
        endTimeComboBox.getItems().addAll(TIME_SLOTS);
        
        // 시간표 그리드 설정
        setupTimetableGrid();
        
        // 과목 목록 테이블
        courseTable = UIStyleManager.createStandardTableView();
        setupCourseTable();
    }
    
    private void setupTimetableGrid() {
        timetableGrid = new GridPane();
        timetableGrid.setHgap(2);
        timetableGrid.setVgap(2);
        timetableGrid.setPadding(new Insets(10));
        timetableGrid.getStyleClass().add("container");
        
        // 헤더 행 (요일)
        Label timeHeader = new Label("시간");
        timeHeader.getStyleClass().add("label-title");
        timeHeader.setAlignment(Pos.CENTER);
        timeHeader.setPrefSize(80, 40);
        timeHeader.setStyle("-fx-background-color: #E9ECEF; -fx-border-color: #DEE2E6;");
        timetableGrid.add(timeHeader, 0, 0);
        
        for (int i = 0; i < DAYS.length; i++) {
            Label dayLabel = new Label(DAYS[i]);
            dayLabel.getStyleClass().add("label-title");
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(100, 40);
            dayLabel.setStyle("-fx-background-color: #E9ECEF; -fx-border-color: #DEE2E6;");
            timetableGrid.add(dayLabel, i + 1, 0);
        }
        
        // 시간별 행 생성
        for (int hour = 0; hour < 12; hour++) {
            String timeText = String.format("%02d:00", 9 + hour);
            Label timeLabel = new Label(timeText);
            timeLabel.getStyleClass().add("label-sub");
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setPrefSize(80, 60);
            timeLabel.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #DEE2E6;");
            timetableGrid.add(timeLabel, 0, hour + 1);
            
            // 각 요일별 셀 생성
            for (int day = 0; day < DAYS.length; day++) {
                Button cellButton = new Button();
                cellButton.setPrefSize(100, 60);
                cellButton.setStyle("-fx-background-color: white; -fx-border-color: #DEE2E6;");
                cellButton.getStyleClass().add("button-secondary");
                
                // 셀 클릭 이벤트 (나중에 구현)
                final int finalDay = day;
                final int finalHour = hour;
                cellButton.setOnAction(e -> handleCellClick(finalDay, finalHour));
                
                timetableGrid.add(cellButton, day + 1, hour + 1);
            }
        }
    }
    
    private void setupCourseTable() {
        TableColumn<CourseItem, String> subjectCol = new TableColumn<>("과목명");
        TableColumn<CourseItem, String> professorCol = new TableColumn<>("교수");
        TableColumn<CourseItem, String> timeCol = new TableColumn<>("시간");
        TableColumn<CourseItem, String> locationCol = new TableColumn<>("강의실");
        
        subjectCol.setPrefWidth(120);
        professorCol.setPrefWidth(80);
        timeCol.setPrefWidth(120);
        locationCol.setPrefWidth(100);
        
        courseTable.getColumns().addAll(subjectCol, professorCol, timeCol, locationCol);
        courseTable.setPrefHeight(200);
    }
    
    private void setupLayout() {
        // 제목
        Label titleLabel = UIStyleManager.createTitleLabel("📅 시간표 관리");
        
        // 메인 컨텐츠를 좌우로 분할
        HBox mainContent = new HBox(UIStyleManager.STANDARD_SPACING);
        
        // 왼쪽: 시간표 그리드
        VBox leftPane = new VBox(UIStyleManager.STANDARD_SPACING);
        leftPane.setPrefWidth(750);
        
        Label timetableTitle = UIStyleManager.createSubLabel("🗓️ 주간 시간표");
        
        ScrollPane timetableScroll = UIStyleManager.createStandardScrollPane(timetableGrid);
        timetableScroll.setPrefHeight(500);
        
        leftPane.getChildren().addAll(timetableTitle, timetableScroll);
        
        // 오른쪽: 입력 폼과 과목 목록
        VBox rightPane = new VBox(UIStyleManager.STANDARD_SPACING);
        rightPane.setPrefWidth(350);
        
        // 입력 폼
        VBox formContainer = UIStyleManager.createStandardContainer();
        Label formTitle = UIStyleManager.createSubLabel("➕ 과목 추가");
        
        GridPane formGrid = UIStyleManager.createFormGrid();
        formGrid.add(new Label("과목명:"), 0, 0);
        formGrid.add(subjectField, 1, 0);
        formGrid.add(new Label("교수님:"), 0, 1);
        formGrid.add(professorField, 1, 1);
        formGrid.add(new Label("강의실:"), 0, 2);
        formGrid.add(locationField, 1, 2);
        formGrid.add(new Label("요일:"), 0, 3);
        formGrid.add(dayComboBox, 1, 3);
        formGrid.add(new Label("시작시간:"), 0, 4);
        formGrid.add(startTimeComboBox, 1, 4);
        formGrid.add(new Label("종료시간:"), 0, 5);
        formGrid.add(endTimeComboBox, 1, 5);
        
        // 버튼 영역
        HBox buttonBox = UIStyleManager.createStandardHBox();
        Button addButton = UIStyleManager.createPrimaryButton("추가");
        Button editButton = UIStyleManager.createSecondaryButton("수정");
        Button deleteButton = UIStyleManager.createSecondaryButton("삭제");
        
        UIStyleManager.applyTooltip(addButton, "시간표에 과목을 추가합니다");
        UIStyleManager.applyTooltip(editButton, "선택된 과목을 수정합니다");
        UIStyleManager.applyTooltip(deleteButton, "선택된 과목을 삭제합니다");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton);
        
        formContainer.getChildren().addAll(formTitle, formGrid, buttonBox);
        
        // 과목 목록 테이블
        VBox tableContainer = UIStyleManager.createStandardContainer();
        Label tableTitle = UIStyleManager.createSubLabel("📚 수강 과목 목록");
        tableContainer.getChildren().addAll(tableTitle, courseTable);
        
        rightPane.getChildren().addAll(formContainer, tableContainer);
        
        // 전체 레이아웃
        mainContent.getChildren().addAll(leftPane, rightPane);
        this.getChildren().addAll(titleLabel, mainContent);
    }
    
    private void handleCellClick(int day, int hour) {
        // 시간표 셀 클릭 처리 (추후 구현)
        String dayName = DAYS[day];
        String time = String.format("%02d:00", 9 + hour);
        System.out.println("Clicked: " + dayName + " " + time);
    }
    
    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
    
    // 더미 데이터 클래스
    public static class CourseItem {
        private String subject;
        private String professor;
        private String time;
        private String location;
        
        public CourseItem(String subject, String professor, String time, String location) {
            this.subject = subject;
            this.professor = professor;
            this.time = time;
            this.location = location;
        }
        
        // getters and setters
        public String getSubject() { return subject; }
        public String getProfessor() { return professor; }
        public String getTime() { return time; }
        public String getLocation() { return location; }
    }
}