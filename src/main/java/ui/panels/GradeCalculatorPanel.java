package ui.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import common.model.Grade;
import gradecalc.GradeCalculator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

public class GradeCalculatorPanel extends VBox {

    private TableView<GradeItem> gradeTable;
    private TextField subjectField;
    private TextField creditsField;
    private ComboBox<String> gradeComboBox;
    private TextField pointField;
    private CheckBox majorCheckBox;
    private Button addButton;
    private Button editButton;
    private Button deleteButton;
    private Button calculateButton;
    private Label gpaLabel;
    private Label totalCreditsLabel;
    private Label avgGradeLabel;
    private Label highestGradeLabel;
    private CategoryAxis xAxis;
    private BarChart<String, Number> distributionChart;

    public GradeCalculatorPanel() {
        applyStyles();
        initInputs();
        initButtons();
        initTable();
        initResultLabels();
        initChart();
        layoutComponents();
        hookHandlers();
    }

    private void initInputs() {
        subjectField  = new TextField();
        creditsField  = new TextField();
        gradeComboBox = new ComboBox<>();
        pointField    = new TextField();
        pointField.setEditable(false);
        majorCheckBox = new CheckBox("전공");

        gradeComboBox.getItems().addAll("A+","A","B+","B","C","D","F","P","U");
        gradeComboBox.setOnAction(e -> {
            String sel = gradeComboBox.getValue();
            if ("P".equals(sel) || "U".equals(sel)) {
                pointField.clear();
            } else {
                pointField.setText(String.valueOf(mapGradeToPoint(sel)));
            }
        });
    }

    private void initButtons() {
        addButton       = UIStyleManager.createPrimaryButton("과목 추가");
        editButton      = UIStyleManager.createSecondaryButton("수정");
        deleteButton    = UIStyleManager.createSecondaryButton("삭제");
        calculateButton = UIStyleManager.createPrimaryButton("학점 계산");
        UIStyleManager.applyTooltip(calculateButton, "전체 평점평균을 계산합니다");
    }

    private void initTable() {
        gradeTable = new TableView<>();
        gradeTable.getColumns().clear();
        TableColumn<GradeItem, String> colName    = new TableColumn<>("과목명");
        TableColumn<GradeItem, Integer> colCredit = new TableColumn<>("학점");
        TableColumn<GradeItem, String> colGrade   = new TableColumn<>("성적");
        TableColumn<GradeItem, Double> colPoint   = new TableColumn<>("평점");
        TableColumn<GradeItem, Boolean> colMajor  = new TableColumn<>("전공");

        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));
        colCredit.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCredits()).asObject());
        colGrade.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrade()));
        colPoint.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPoint()).asObject());
        colMajor.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isMajor()).asObject());

        gradeTable.getColumns().addAll(colName, colCredit, colGrade, colPoint, colMajor);
        gradeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void initResultLabels() {
        gpaLabel          = new Label("평점평균: 0.00");
        totalCreditsLabel = new Label("총 이수학점: 0");
        avgGradeLabel     = new Label("평균 성적: -");
        highestGradeLabel = new Label("최고 성적: -");
    }

    private void initChart() {
        xAxis = new CategoryAxis();
        xAxis.setLabel("과목명");
        xAxis.setTickLabelsVisible(false);
        xAxis.setTickMarkVisible(false);

        NumberAxis yAxis = new NumberAxis(0, 5, 1);
        yAxis.setLabel("평점");

        distributionChart = new BarChart<>(xAxis, yAxis);
        distributionChart.setTitle("학점분포");
        distributionChart.setLegendVisible(true);
        distributionChart.setLegendSide(Side.RIGHT);

        // ← 이 두 줄을 이렇게 바꿔주세요!
        distributionChart.setCategoryGap(0);  // 카테고리 간격을 0으로
        distributionChart.setBarGap(0);       // 막대 간격도 0으로

        // 범례 투명 처리(기존 코드)
        distributionChart.applyCss();
        distributionChart.layout();
        Node legendNode = distributionChart.lookup(".chart-legend");
        if (legendNode instanceof Region) {
            Region legendRegion = (Region) legendNode;
            legendRegion.setBackground(Background.EMPTY);
            legendRegion.setBorder(Border.EMPTY);
        }
    }


    private void layoutComponents() {
        // 입력 및 버튼 레이아웃
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));
        inputGrid.add(new Label("과목명"), 0, 0);
        inputGrid.add(subjectField, 1, 0);
        inputGrid.add(new Label("성적"), 2, 0);
        inputGrid.add(gradeComboBox, 3, 0);
        inputGrid.add(majorCheckBox, 4, 0);
        inputGrid.add(new Label("학점"), 0, 1);
        inputGrid.add(creditsField, 1, 1);
        inputGrid.add(new Label("평점"), 2, 1);
        inputGrid.add(pointField, 3, 1);

        HBox buttonBox = new HBox(10, addButton, editButton, deleteButton, calculateButton);
        buttonBox.setPadding(new Insets(10));

        VBox leftBox = new VBox(10, inputGrid, buttonBox, gradeTable);
        leftBox.setPadding(new Insets(10));

        VBox rightBox = new VBox(10, gpaLabel, totalCreditsLabel, avgGradeLabel, highestGradeLabel, distributionChart);
        rightBox.setPadding(new Insets(10));

        this.getChildren().setAll(new HBox(20, leftBox, rightBox));
    }

    private void hookHandlers() {
        addButton.setOnAction(e -> addItem());
        editButton.setOnAction(e -> editItem());
        deleteButton.setOnAction(e -> deleteItem());
        calculateButton.setOnAction(e -> calculateAndDraw());
    }

    private void addItem() {
        String subject = subjectField.getText();
        int credits = Integer.parseInt(creditsField.getText());
        String grade = gradeComboBox.getValue();
        double point = grade.equals("P") || grade.equals("U") ? 0.0 : mapGradeToPoint(grade);
        boolean major = majorCheckBox.isSelected();
        gradeTable.getItems().add(new GradeItem(subject, credits, grade, point, major));
        clearInputs();
    }

    private void editItem() {
        String subj = subjectField.getText();
        for (GradeItem item : gradeTable.getItems()) {
            if (item.getSubject().equals(subj)) {
                item.setCredits(Integer.parseInt(creditsField.getText()));
                item.setGrade(gradeComboBox.getValue());
                item.setPoint(gradeComboBox.getValue().equals("P") || gradeComboBox.getValue().equals("U") ? 0.0 : mapGradeToPoint(gradeComboBox.getValue()));
                item.setMajor(majorCheckBox.isSelected());
                gradeTable.refresh();
                break;
            }
        }
        clearInputs();
    }

    private void deleteItem() {
        String subj = subjectField.getText();
        gradeTable.getItems().removeIf(item -> item.getSubject().equals(subj));
        clearInputs();
    }

    private void calculateAndDraw() {
        // 결과 계산
        List<Grade> grades = gradeTable.getItems().stream()
                .map(item -> new Grade(item.getSubject(), item.getGrade(), item.getPoint(), item.getCredits(), item.isMajor()))
                .collect(Collectors.toList());
        GradeCalculator calc = new GradeCalculator();
        double gpa = calc.calculateGPA(grades);
        int total = calc.calculateTotalCredits(grades);
        String avgGrade = grades.isEmpty() ? "-" : mapPointToGrade(gpa);
        OptionalDouble maxGpaOpt = grades.stream()
                .mapToDouble(Grade::getGpa)
                .max();
        String highest = maxGpaOpt.isPresent()
            ? mapPointToGrade(maxGpaOpt.getAsDouble())
            : "-";

        gpaLabel.setText(String.format("평점평균: %.2f", gpa));
        totalCreditsLabel.setText("총 이수학점: " + total);
        avgGradeLabel.setText("평균 성적: " + avgGrade);
        highestGradeLabel.setText("최고 성적: " + highest);

        // 차트 데이터 정렬하여 그리기
        distributionChart.getData().clear();
        List<GradeItem> sorted = new ArrayList<>(gradeTable.getItems());
        sorted.sort(Comparator.comparingDouble(GradeItem::getPoint));
        for (GradeItem item : sorted) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(item.getSubject());
            series.getData().add(new XYChart.Data<>(item.getSubject(), item.getPoint()));
            distributionChart.getData().add(series);
        }
    }

    private double mapGradeToPoint(String grade) {
        switch (grade) {
            case "A+": return 4.5;
            case "A":  return 4.0;
            case "B+": return 3.5;
            case "B":  return 3.0;
            case "C":  return 2.5;
            case "D":  return 2.0;
            default:   return 0.0;
        }
    }

    private String mapPointToGrade(double point) {
        if (point >= 4.5) return "A+";
        if (point >= 4.0) return "A";
        if (point >= 3.5) return "B+";
        if (point >= 3.0) return "B";
        if (point >= 2.5) return "C";
        if (point >= 2.0) return "D";
        return "F";
    }

    private void clearInputs() {
        subjectField.clear();
        creditsField.clear();
        gradeComboBox.setValue(null);
        pointField.clear();
        majorCheckBox.setSelected(false);
    }

    public static class GradeItem {
        private final SimpleStringProperty subject;
        private final SimpleIntegerProperty credits;
        private final SimpleStringProperty grade;
        private final SimpleDoubleProperty point;
        private final SimpleBooleanProperty major;

        public GradeItem(String subject, int credits, String grade, double point, boolean major) {
            this.subject = new SimpleStringProperty(subject);
            this.credits = new SimpleIntegerProperty(credits);
            this.grade   = new SimpleStringProperty(grade);
            this.point   = new SimpleDoubleProperty(point);
            this.major   = new SimpleBooleanProperty(major);
        }

        public String getSubject() { return subject.get(); }
        public void setSubject(String val) { subject.set(val); }
        public int    getCredits() { return credits.get(); }
        public void setCredits(int val) { credits.set(val); }
        public String getGrade()   { return grade.get(); }
        public void setGrade(String val) { grade.set(val); }
        public double getPoint()   { return point.get(); }
        public void setPoint(double val) { point.set(val); }
        public boolean isMajor()   { return major.get(); }
        public void setMajor(boolean val) { major.set(val); }
    }

    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
}
