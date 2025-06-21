package ui.panels;

import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

/**
 * 학점계산 탭의 UI 패널
 */
public class GradeCalculatorPanel extends VBox {
    
    private TableView<GradeItem> gradeTable;
    private TextField subjectField;
    private TextField creditsField;
    private ComboBox<String> gradeComboBox;
    private Label gpaLabel;
    private Label totalCreditsLabel;
    private PieChart gradeChart;
    
    public GradeCalculatorPanel() {
        super(UIStyleManager.STANDARD_SPACING);
        initializeComponents();
        setupLayout();
        applyStyles();
    }
    
    private void initializeComponents() {
        // 입력 폼 컴포넌트
        subjectField = UIStyleManager.createStandardTextField("과목명");
        creditsField = UIStyleManager.createStandardTextField("학점 (예: 3)");
        gradeComboBox = UIStyleManager.createStandardComboBox();
        gradeComboBox.getItems().addAll("A+", "A", "B+", "B", "C+", "C", "D+", "D", "F");
        
        // 결과 표시 라벨
        gpaLabel = UIStyleManager.createTitleLabel("평점평균: 0.00");
        totalCreditsLabel = UIStyleManager.createSubLabel("총 학점: 0");
        
        // 테이블 설정
        gradeTable = UIStyleManager.createStandardTableView();
        setupTableColumns();
        
        // 차트 설정
        gradeChart = new PieChart();
        gradeChart.getStyleClass().add("chart");
        gradeChart.setTitle("학점 분포");
    }
    
    private void setupTableColumns() {
        TableColumn<GradeItem, String> subjectCol = new TableColumn<>("과목명");
        TableColumn<GradeItem, Integer> creditsCol = new TableColumn<>("학점");
        TableColumn<GradeItem, String> gradeCol = new TableColumn<>("성적");
        TableColumn<GradeItem, Double> pointCol = new TableColumn<>("평점");
        
        subjectCol.setPrefWidth(150);
        creditsCol.setPrefWidth(60);
        gradeCol.setPrefWidth(60);
        pointCol.setPrefWidth(70);
        
        gradeTable.getColumns().addAll(subjectCol, creditsCol, gradeCol, pointCol);
    }
    
    private void setupLayout() {
        // 제목
        Label titleLabel = UIStyleManager.createTitleLabel("📊 학점 계산기");
        
        // 입력 폼과 결과를 좌우로 분할
        HBox mainContent = new HBox(UIStyleManager.STANDARD_SPACING);
        
        // 왼쪽: 입력 폼과 테이블
        VBox leftPane = new VBox(UIStyleManager.STANDARD_SPACING);
        leftPane.setPrefWidth(500);
        
        // 입력 폼
        GridPane formGrid = UIStyleManager.createFormGrid();
        formGrid.add(new Label("과목명:"), 0, 0);
        formGrid.add(subjectField, 1, 0);
        formGrid.add(new Label("학점:"), 0, 1);
        formGrid.add(creditsField, 1, 1);
        formGrid.add(new Label("성적:"), 0, 2);
        formGrid.add(gradeComboBox, 1, 2);
        
        // 버튼 영역
        HBox buttonBox = UIStyleManager.createStandardHBox();
        Button addButton = UIStyleManager.createPrimaryButton("과목 추가");
        Button editButton = UIStyleManager.createSecondaryButton("수정");
        Button deleteButton = UIStyleManager.createSecondaryButton("삭제");
        Button calculateButton = UIStyleManager.createPrimaryButton("학점 계산");
        
        UIStyleManager.applyTooltip(addButton, "새 과목을 추가합니다");
        UIStyleManager.applyTooltip(calculateButton, "전체 평점평균을 계산합니다");
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, calculateButton);
        
        // 테이블
        VBox tableContainer = UIStyleManager.createStandardContainer();
        Label tableTitle = UIStyleManager.createSubLabel("수강 과목 목록");
        gradeTable.setPrefHeight(300);
        tableContainer.getChildren().addAll(tableTitle, gradeTable);
        
        // 입력 폼 컨테이너
        VBox formContainer = UIStyleManager.createStandardContainer();
        formContainer.getChildren().addAll(formGrid, buttonBox);
        
        leftPane.getChildren().addAll(formContainer, tableContainer);
        
        // 오른쪽: 결과 및 차트
        VBox rightPane = new VBox(UIStyleManager.STANDARD_SPACING);
        rightPane.setPrefWidth(400);
        
        // 결과 표시
        VBox resultContainer = UIStyleManager.createStandardContainer();
        Label resultTitle = UIStyleManager.createSubLabel("📈 계산 결과");
        
        // 결과 그리드
        GridPane resultGrid = new GridPane();
        resultGrid.setHgap(10);
        resultGrid.setVgap(10);
        resultGrid.setPadding(new Insets(10));
        
        resultGrid.add(gpaLabel, 0, 0, 2, 1);
        resultGrid.add(totalCreditsLabel, 0, 1, 2, 1);
        
        // 추가 통계 정보
        Label avgGradeLabel = UIStyleManager.createSubLabel("평균 성적: -");
        Label highestGradeLabel = UIStyleManager.createSubLabel("최고 성적: -");
        resultGrid.add(avgGradeLabel, 0, 2, 2, 1);
        resultGrid.add(highestGradeLabel, 0, 3, 2, 1);
        
        resultContainer.getChildren().addAll(resultTitle, resultGrid);
        
        // 차트 컨테이너
        VBox chartContainer = UIStyleManager.createStandardContainer();
        Label chartTitle = UIStyleManager.createSubLabel("📊 성적 분포");
        gradeChart.setPrefHeight(250);
        chartContainer.getChildren().addAll(chartTitle, gradeChart);
        
        rightPane.getChildren().addAll(resultContainer, chartContainer);
        
        // 전체 레이아웃
        mainContent.getChildren().addAll(leftPane, rightPane);
        
        this.getChildren().addAll(titleLabel, mainContent);
    }
    
    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
    
    // 더미 데이터 클래스
    public static class GradeItem {
        private String subject;
        private Integer credits;
        private String grade;
        private Double point;
        
        public GradeItem(String subject, Integer credits, String grade, Double point) {
            this.subject = subject;
            this.credits = credits;
            this.grade = grade;
            this.point = point;
        }
        
        // getters and setters
        public String getSubject() { return subject; }
        public Integer getCredits() { return credits; }
        public String getGrade() { return grade; }
        public Double getPoint() { return point; }
    }
}