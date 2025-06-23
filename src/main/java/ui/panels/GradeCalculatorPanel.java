package ui.panels;

import java.util.List;
import java.util.stream.Collectors;

import common.database.DatabaseManager;
import common.listeners.DataChangeListener;
import common.model.Subject;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
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

public class GradeCalculatorPanel extends VBox implements DataChangeListener {

    private DatabaseManager dbManager;

    // --- 상단 입력부
    private TextField        subjectField;
    private TextField        creditField;
    private ComboBox<String> gradeComboBox;
    private ComboBox<String> categoryComboBox;

    private Button addCreditButton;
    private Button deleteCreditButton;
    private Button calculateButton;
    private Button clearAllButton;

    // --- 통계 표시
    private Label totalGpaLabel;
    private Label majorGpaLabel;
    private Label totalCreditsLabel;
    private Label majorCreditsLabel;
    private Label statusLabel;

    // --- 테이블 & 차트
    private TableView<GradeItem>    gradeTable;
    private CategoryAxis            xAxis;
    private NumberAxis              yAxis;
    private BarChart<String, Number> distributionChart;

    public GradeCalculatorPanel() {
        dbManager = DatabaseManager.getInstance();
        
        // 데이터 변경 리스너로 등록
        dbManager.addDataChangeListener(this);

        initializeFields();
        initButtons();
        initLabels();
        initTable();
        initChart();
        layoutComponents();
        hookHandlers();

        // 초기 데이터 로드
        refreshData();

        // 스타일
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }

    private void initializeFields() {
        subjectField  = UIStyleManager.createStandardTextField("과목명");
        creditField   = UIStyleManager.createStandardTextField("학점");
        
        gradeComboBox = UIStyleManager.createStandardComboBox();
        gradeComboBox.getItems().addAll(
            "A+","A","B+","B","C+","C","D+","D","F","P","U"
        );

        categoryComboBox = UIStyleManager.createStandardComboBox();
        categoryComboBox.getItems().addAll("전공필수", "전공선택", "교양", "자유선택");
        categoryComboBox.setValue("전공필수");

        gradeTable = new TableView<>();
    }

    private void initButtons() {
        addCreditButton    = UIStyleManager.createPrimaryButton("성적 추가");
        deleteCreditButton = UIStyleManager.createSecondaryButton("성적 삭제");
        calculateButton    = UIStyleManager.createPrimaryButton("GPA 계산");
        clearAllButton     = UIStyleManager.createSecondaryButton("전체 초기화");
    }
    
    private void initLabels() {
        totalGpaLabel = UIStyleManager.createTitleLabel("전체 GPA: 0.00");
        majorGpaLabel = UIStyleManager.createSubLabel("전공 GPA: 0.00");
        totalCreditsLabel = UIStyleManager.createSubLabel("총 학점: 0");
        majorCreditsLabel = UIStyleManager.createSubLabel("전공 학점: 0");
        statusLabel = UIStyleManager.createSubLabel("📊 등록된 과목: 0개");
    }

    private void initTable() {
        TableColumn<GradeItem, String>  colName     = new TableColumn<>("과목명");
        TableColumn<GradeItem, Integer> colCredit   = new TableColumn<>("학점");
        TableColumn<GradeItem, String>  colGrade    = new TableColumn<>("성적");
        TableColumn<GradeItem, Double>  colPoint    = new TableColumn<>("평점");
        TableColumn<GradeItem, String>  colCategory = new TableColumn<>("분류");
        TableColumn<GradeItem, String>  colMajor    = new TableColumn<>("전공");

        colName    .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));
        colCredit  .setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCredits()).asObject());
        colGrade   .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrade()));
        colPoint   .setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPoint()).asObject());
        colCategory.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        colMajor   .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isMajor() ? "O" : "X"));

        // 컬럼 너비 설정
        colName.setPrefWidth(120);
        colCredit.setPrefWidth(60);
        colGrade.setPrefWidth(60);
        colPoint.setPrefWidth(60);
        colCategory.setPrefWidth(80);
        colMajor.setPrefWidth(60);

        gradeTable.getColumns().addAll(colName, colCredit, colGrade, colPoint, colCategory, colMajor);
        gradeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        gradeTable.setPrefHeight(250);
    }

    private void initChart() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        yAxis.setLabel("평점");
        distributionChart = new BarChart<>(xAxis, yAxis);
        distributionChart.setTitle("과목별 평점 분포");
        distributionChart.setLegendSide(Side.BOTTOM);
        distributionChart.setAnimated(true);
        distributionChart.setPrefHeight(250);
    }

    private void layoutComponents() {
        Label title = UIStyleManager.createTitleLabel("📊 학점 계산기");
        
        // 상단 통계 정보
        VBox statsBox = UIStyleManager.createStandardContainer();
        HBox gpaBox = new HBox(20);
        gpaBox.getChildren().addAll(totalGpaLabel, majorGpaLabel);
        
        HBox creditsBox = new HBox(20);
        creditsBox.getChildren().addAll(totalCreditsLabel, majorCreditsLabel);
        
        statsBox.getChildren().addAll(gpaBox, creditsBox, statusLabel);

        // 입력 폼
        VBox formContainer = UIStyleManager.createStandardContainer();
        Label formTitle = UIStyleManager.createSubLabel("➕ 성적 입력");
        
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(0, 0, 10, 0));

        inputGrid.add(new Label("과목명:"),    0, 0);
        inputGrid.add(subjectField,           1, 0);
        inputGrid.add(new Label("학점:"),      2, 0);
        inputGrid.add(creditField,            3, 0);
        inputGrid.add(new Label("성적:"),      0, 1);
        inputGrid.add(gradeComboBox,          1, 1);
        inputGrid.add(new Label("분류:"),      2, 1);
        inputGrid.add(categoryComboBox,       3, 1);

        // 버튼 영역
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addCreditButton, deleteCreditButton, calculateButton, clearAllButton);

        formContainer.getChildren().addAll(formTitle, inputGrid, buttonBox);

        // 메인 레이아웃
        HBox mainContent = new HBox(20);
        
        // 왼쪽: 폼 + 테이블
        VBox leftBox = new VBox(15);
        leftBox.setPrefWidth(600);
        leftBox.getChildren().addAll(formContainer, gradeTable);
        
        // 오른쪽: 차트
        VBox rightBox = new VBox(15);
        rightBox.setPrefWidth(400);
        rightBox.getChildren().add(distributionChart);

        mainContent.getChildren().addAll(leftBox, rightBox);
        
        this.getChildren().addAll(title, statsBox, mainContent);
    }

    private void hookHandlers() {
        // 테이블 클릭 시 폼에 데이터 로드
        gradeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                subjectField.setText(newSel.getSubject());
                creditField.setText(String.valueOf(newSel.getCredits()));
                gradeComboBox.setValue(newSel.getGrade().isEmpty() ? null : newSel.getGrade());
                categoryComboBox.setValue(newSel.getCategory());
            }
        });

        // 성적 추가/수정
        addCreditButton.setOnAction(e -> {
            String subj   = subjectField.getText().trim();
            String letter = gradeComboBox.getValue();
            String creditStr = creditField.getText().trim();
            String category = categoryComboBox.getValue();
            
            if (subj.isEmpty()) {
                showAlert("입력 오류", "과목명을 입력해주세요.");
                return;
            }
            
            if (letter == null) {
                showAlert("입력 오류", "성적을 선택해주세요.");
                return;
            }
            
            int credits = 3; // 기본값
            try {
                if (!creditStr.isEmpty()) {
                    credits = Integer.parseInt(creditStr);
                }
            } catch (NumberFormatException ex) {
                showAlert("입력 오류", "학점은 숫자로 입력해주세요.");
                return;
            }
            
            // 기존 항목 찾기
            boolean updated = false;
            for (GradeItem item : gradeTable.getItems()) {
                if (item.getSubject().equals(subj)) {
                    item.setGrade(letter);
                    item.setPoint(mapGradeToPoint(letter));
                    item.setCredits(credits);
                    item.setCategory(category);
                    item.setMajor(isMajorCategory(category));
                    updated = true;
                    break;
                }
            }
            
            // 새 항목 추가
            if (!updated) {
                gradeTable.getItems().add(new GradeItem(
                    subj, credits, letter, mapGradeToPoint(letter), category, isMajorCategory(category)
                ));
            }
            
            gradeTable.refresh();
            updateChart();
            calculateStatistics();
            clearForm();
            showAlert("성공", updated ? "성적이 수정되었습니다!" : "성적이 추가되었습니다!");
        });

        // 성적 삭제
        deleteCreditButton.setOnAction(e -> {
            GradeItem selected = gradeTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("선택 오류", "삭제할 항목을 선택해주세요.");
                return;
            }
            
            gradeTable.getItems().remove(selected);
            gradeTable.refresh();
            updateChart();
            calculateStatistics();
            clearForm();
            showAlert("성공", "성적이 삭제되었습니다!");
        });
        
        // GPA 계산
        calculateButton.setOnAction(e -> {
            calculateStatistics();
            updateChart();
            showAlert("계산 완료", "GPA 계산이 완료되었습니다!");
        });
        
        // 전체 초기화
        clearAllButton.setOnAction(e -> {
            gradeTable.getItems().clear();
            updateChart();
            calculateStatistics();
            clearForm();
            showAlert("초기화", "모든 성적이 삭제되었습니다!");
        });
    }
    
    private void clearForm() {
        subjectField.clear();
        creditField.clear();
        gradeComboBox.setValue(null);
        categoryComboBox.setValue("전공필수");
        gradeTable.getSelectionModel().clearSelection();
    }
    
    private void calculateStatistics() {
        List<GradeItem> items = gradeTable.getItems();
        
        // 전체 GPA 계산
        double totalPoints = 0.0;
        int totalCredits = 0;
        
        // 전공 GPA 계산
        double majorPoints = 0.0;
        int majorCredits = 0;
        
        for (GradeItem item : items) {
            if (!item.getGrade().isEmpty() && !item.getGrade().equals("F")) {
                totalPoints += item.getPoint() * item.getCredits();
                totalCredits += item.getCredits();
                
                if (item.isMajor()) {
                    majorPoints += item.getPoint() * item.getCredits();
                    majorCredits += item.getCredits();
                }
            }
        }
        
        double totalGpa = totalCredits > 0 ? totalPoints / totalCredits : 0.0;
        double majorGpa = majorCredits > 0 ? majorPoints / majorCredits : 0.0;
        
        // UI 업데이트
        totalGpaLabel.setText(String.format("전체 GPA: %.2f", totalGpa));
        majorGpaLabel.setText(String.format("전공 GPA: %.2f", majorGpa));
        totalCreditsLabel.setText(String.format("총 학점: %d", totalCredits));
        majorCreditsLabel.setText(String.format("전공 학점: %d", majorCredits));
        statusLabel.setText(String.format("📊 등록된 과목: %d개", items.size()));
        
        // GPA 등급 표시
        String gpaGrade = getGPAGrade(totalGpa);
        totalGpaLabel.setText(String.format("전체 GPA: %.2f (%s)", totalGpa, gpaGrade));
    }
    
    private String getGPAGrade(double gpa) {
        if (gpa >= 4.0) return "최우수";
        else if (gpa >= 3.5) return "우수";
        else if (gpa >= 3.0) return "보통";
        else if (gpa >= 2.5) return "미흡";
        else return "경고";
    }

    /**
     * 데이터 새로고침 (외부에서 호출 가능)
     */
    public void refreshData() {
        loadSubjects();
        calculateStatistics();
        updateChart();
        System.out.println("🔄 학점 계산기 데이터 새로고침 완료");
    }

    /** DB에서 시간표 과목 불러와 테이블에 세팅 */
    private void loadSubjects() {
        List<Subject> subjects = dbManager.getAllSubjects();
        
        // 기존 데이터 보존하면서 새로운 과목들만 추가
        List<String> existingSubjects = gradeTable.getItems().stream()
            .map(GradeItem::getSubject)
            .collect(Collectors.toList());
        
        for (Subject s : subjects) {
            if (!existingSubjects.contains(s.getName())) {
                gradeTable.getItems().add(new GradeItem(
                    s.getName(),
                    s.getCredits(),
                    "",                   // 초기 성적 없음
                    0.0,                  // 초기 평점 0.0
                    s.getCategory() != null ? s.getCategory() : "전공필수",
                    isMajorCategory(s.getCategory())
                ));
            }
        }
        
        // 시간표에서 삭제된 과목들 제거 (성적이 입력되지 않은 경우만)
        List<String> currentSubjects = subjects.stream()
            .map(Subject::getName)
            .collect(Collectors.toList());
            
        gradeTable.getItems().removeIf(item -> 
            !currentSubjects.contains(item.getSubject()) && 
            (item.getGrade() == null || item.getGrade().isEmpty())
        );
    }

    /** Subject.category가 "전공필수" 또는 "전공선택"인지 체크 */
    private boolean isMajorCategory(String category) {
        return "전공필수".equals(category) || "전공선택".equals(category);
    }

    /** 성적 문자열을 평점(double)으로 매핑 */
    private double mapGradeToPoint(String grade) {
        switch (grade) {
            case "A+": return 4.5;
            case "A":  return 4.0;
            case "B+": return 3.5;
            case "B":  return 3.0;
            case "C+": return 2.5;
            case "C":  return 2.0;
            case "D+": return 1.5;
            case "D":  return 1.0;
            default:   return 0.0;  // F, P, U 모두 0.0 처리
        }
    }

    /** 현재 테이블 데이터를 기반으로 차트를 초기화(갱신) */
    private void updateChart() {
        distributionChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("평점");
        
        for (GradeItem item : gradeTable.getItems()) {
            if (!item.getGrade().isEmpty()) {
                series.getData().add(new XYChart.Data<>(item.getSubject(), item.getPoint()));
            }
        }
        
        distributionChart.getData().add(series);
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
            System.out.println("🔄 GradeCalculatorPanel: 과목 변경 감지 (" + changeType + ")");
            refreshData();
        });
    }

    /**
     * 패널이 닫힐 때 리스너 해제
     */
    public void dispose() {
        dbManager.removeDataChangeListener(this);
        System.out.println("🔗 GradeCalculatorPanel 리스너 해제");
    }

    /** TableView용 데이터 모델 */
    public static class GradeItem {
        private final SimpleStringProperty  subject;
        private final SimpleIntegerProperty credits;
        private final SimpleStringProperty  grade;
        private final SimpleDoubleProperty  point;
        private final SimpleStringProperty  category;
        private final SimpleBooleanProperty major;

        public GradeItem(String subject, int credits, String grade, double point, String category, boolean major) {
            this.subject  = new SimpleStringProperty(subject);
            this.credits  = new SimpleIntegerProperty(credits);
            this.grade    = new SimpleStringProperty(grade);
            this.point    = new SimpleDoubleProperty(point);
            this.category = new SimpleStringProperty(category);
            this.major    = new SimpleBooleanProperty(major);
        }

        public String  getSubject()  { return subject.get(); }
        public int     getCredits()  { return credits.get(); }
        public String  getGrade()    { return grade.get(); }
        public double  getPoint()    { return point.get(); }
        public String  getCategory() { return category.get(); }
        public boolean isMajor()     { return major.get(); }

        public void setSubject(String s)  { subject.set(s); }
        public void setGrade(String g)    { grade.set(g); }
        public void setPoint(double p)    { point.set(p); }
        public void setCredits(int c)     { credits.set(c); }
        public void setCategory(String c) { category.set(c); }
        public void setMajor(boolean m)   { major.set(m); }
    }
}