// src/main/java/ui/panels/GradeCalculatorPanel.java
package ui.panels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import common.database.DatabaseManager;
import common.model.Grade;
import gradecalc.GradeCalculator;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
    private Button saveButton;
    private Button loadButton;
    private Label gpaLabel;
    private Label totalCreditsLabel;
    private Label avgGradeLabel;
    private Label highestGradeLabel;
    private CategoryAxis xAxis;
    private BarChart<String, Number> distributionChart;

    public GradeCalculatorPanel() {
        initializeFields();
        initButtons();
        initTable();
        initResultLabels();
        initChart();
        layoutComponents();
        hookHandlers();
        applyStyles();
    }

    private void initializeFields() {
        // UIStyleManager.createStandardTextField 사용:contentReference[oaicite:2]{index=2}
        subjectField  = UIStyleManager.createStandardTextField("과목명 입력");
        creditsField  = UIStyleManager.createStandardTextField("학점 입력");
        gradeComboBox = UIStyleManager.createStandardComboBox();           //● 콤보박스 생성:contentReference[oaicite:3]{index=3}
        gradeComboBox.getItems().addAll("A+","A","B+","B","C+","C","D+","D","F","P","U");
        gradeComboBox.setOnAction(e -> {
            String sel = gradeComboBox.getValue();
            if ("P".equals(sel) || "U".equals(sel)) {
                pointField.clear();
            } else {
                pointField.setText(String.valueOf(mapGradeToPoint(sel)));
            }
        });
        pointField     = UIStyleManager.createStandardTextField("평점");     // 읽기 전용 설정은 아래에서
        pointField.setEditable(false);
        majorCheckBox  = new CheckBox("전공");
        gradeTable     = new TableView<>();
        gpaLabel       = new Label("평점평균: 0.00");
        totalCreditsLabel = new Label("총 이수학점: 0");
        avgGradeLabel     = new Label("평균 성적: -");
        highestGradeLabel = new Label("최고 성적: -");
    }

    private void initButtons() {
        addButton       = UIStyleManager.createPrimaryButton("과목 추가");
        editButton      = UIStyleManager.createSecondaryButton("수정");
        deleteButton    = UIStyleManager.createSecondaryButton("삭제");
        calculateButton = UIStyleManager.createPrimaryButton("학점 계산");
        UIStyleManager.applyTooltip(calculateButton, "전체 평점평균을 계산합니다");
        saveButton      = UIStyleManager.createSecondaryButton("저장");
        loadButton      = UIStyleManager.createSecondaryButton("불러오기");
    }

    private void initTable() {
        gradeTable.getColumns().clear();
        TableColumn<GradeItem, String>  colName   = new TableColumn<>("과목명");
        TableColumn<GradeItem, Integer> colCredit = new TableColumn<>("학점");
        TableColumn<GradeItem, String>  colGrade  = new TableColumn<>("성적");
        TableColumn<GradeItem, Double>  colPoint  = new TableColumn<>("평점");
        TableColumn<GradeItem, Boolean> colMajor  = new TableColumn<>("전공");

        colName  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));
        colCredit.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCredits()).asObject());
        colGrade .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrade()));
        colPoint .setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPoint()).asObject());
        colMajor .setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isMajor()).asObject());

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
        distributionChart.setTitle("과목별 평점 분포");
        distributionChart.setLegendSide(Side.BOTTOM);
        distributionChart.setAnimated(false);
    }

    private void layoutComponents() {
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(10));
        inputGrid.add(new Label("과목명"), 0, 0);
        inputGrid.add(subjectField, 1, 0);
        inputGrid.add(new Label("성적"),   2, 0);
        inputGrid.add(gradeComboBox, 3, 0);
        inputGrid.add(majorCheckBox, 4, 0);
        inputGrid.add(new Label("학점"),   0, 1);
        inputGrid.add(creditsField, 1, 1);
        inputGrid.add(new Label("평점"),   2, 1);
        inputGrid.add(pointField,   3, 1);

        HBox buttonBox = new HBox(10,
            addButton, editButton, deleteButton, calculateButton,
            saveButton, loadButton
        );
        buttonBox.setPadding(new Insets(10));

        VBox leftBox  = new VBox(10, inputGrid, buttonBox, gradeTable);
        leftBox.setPadding(new Insets(10));

        VBox rightBox = new VBox(10,
            gpaLabel, totalCreditsLabel, avgGradeLabel, highestGradeLabel, distributionChart
        );
        rightBox.setPadding(new Insets(10));

        this.getChildren().setAll(new HBox(20, leftBox, rightBox));
    }

    private void hookHandlers() {
        addButton      .setOnAction(e -> addItem());
        editButton     .setOnAction(e -> editItem());
        deleteButton   .setOnAction(e -> deleteItem());
        calculateButton.setOnAction(e -> calculateAndDraw());
        saveButton     .setOnAction(e -> saveGrades());
        loadButton     .setOnAction(e -> loadGrades());
    }

    private void addItem() {
        try {
            String subject = subjectField.getText();
            int credits    = Integer.parseInt(creditsField.getText());
            String grade   = gradeComboBox.getValue();
            double point   = ("P".equals(grade) || "U".equals(grade)) 
                             ? 0.0 
                             : mapGradeToPoint(grade);
            boolean major  = majorCheckBox.isSelected();

            gradeTable.getItems().add(new GradeItem(subject, credits, grade, point, major));
            clearInputs();
        } catch (NumberFormatException ex) {
            System.err.println("❌ 학점은 숫자로 입력해야 합니다.");
        }
    }

    private void editItem() {
        String subj = subjectField.getText();
        for (GradeItem item : gradeTable.getItems()) {
            if (item.getSubject().equals(subj)) {
                try {
                    item.setCredits(Integer.parseInt(creditsField.getText()));
                    String sel = gradeComboBox.getValue();
                    item.setGrade(sel);
                    item.setPoint(("P".equals(sel) || "U".equals(sel))
                                  ? 0.0
                                  : mapGradeToPoint(sel));
                    item.setMajor(majorCheckBox.isSelected());
                    gradeTable.refresh();
                } catch (NumberFormatException ex) {
                    System.err.println("❌ 학점은 숫자로 입력해야 합니다.");
                }
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
        List<Grade> grades = gradeTable.getItems().stream()
            .map(item -> new Grade(
                item.getSubject(),
                item.getGrade(),
                item.getPoint(),
                item.getCredits(),
                item.isMajor()
            ))
            .collect(Collectors.toList());

        GradeCalculator calc = new GradeCalculator();
        double gpa = calc.calculateGPA(grades);
        int total = calc.calculateTotalCredits(grades);

        String avgGrade = grades.stream()
            .map(Grade::getLetterGrade)
            .collect(Collectors.groupingBy(g -> g, Collectors.counting()))
            .entrySet().stream()
            .max(Comparator.comparingLong(e -> e.getValue()))
            .map(e -> e.getKey())
            .orElse("-");

        OptionalDouble maxOpt = grades.stream().mapToDouble(Grade::getGpa).max();
        String highest = maxOpt.isPresent()
            ? mapPointToGrade(maxOpt.getAsDouble())
            : "-";

        gpaLabel         .setText(String.format("평점평균: %.2f", gpa));
        totalCreditsLabel.setText("총 이수학점: " + total);
        avgGradeLabel    .setText("평균 성적: " + avgGrade);
        highestGradeLabel.setText("최고 성적: " + highest);

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

    /** 저장 버튼 핸들러 */
    private void saveGrades() {
        List<Grade> grades = gradeTable.getItems().stream()
            .map(item -> new Grade(
                item.getSubject(),
                item.getGrade(),
                item.getPoint(),
                item.getCredits(),
                item.isMajor()
            ))
            .collect(Collectors.toList());
        DatabaseManager.getInstance().saveUserGrades(grades);
        System.out.println("💾 사용자 성적 저장 완료: " + grades.size() + "개");
    }

    /** 불러오기 버튼 핸들러 */
    private void loadGrades() {
        List<Grade> grades = DatabaseManager.getInstance().getUserGrades();
        System.out.println("📂 사용자 성적 로드 완료: " + grades.size() + "개");
        List<GradeItem> items = grades.stream()
            .map(g -> new GradeItem(
                g.getSubjectName(),
                g.getCredit(),
                g.getLetterGrade(),
                g.getGpa(),
                g.isMajor()
            ))
            .collect(Collectors.toList());
        gradeTable.getItems().setAll(items);
    }

    /** 문자 등급 → 평점 매핑 */
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
            default:   return 0.0;
        }
    }

    /** 평점을 문자 등급으로 변환 */
    private String mapPointToGrade(double point) {
        if (point >= 4.5) return "A+";
        if (point >= 4.0) return "A";
        if (point >= 3.5) return "B+";
        if (point >= 3.0) return "B";
        if (point >= 2.5) return "C+";
        if (point >= 2.0) return "C";
        if (point >= 1.5) return "D+";
        if (point >= 1.0) return "D";
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
        private final SimpleStringProperty  subject;
        private final SimpleIntegerProperty credits;
        private final SimpleStringProperty  grade;
        private final SimpleDoubleProperty  point;
        private final SimpleBooleanProperty major;

        public GradeItem(String subject, int credits, String grade, double point, boolean major) {
            this.subject = new SimpleStringProperty(subject);
            this.credits = new SimpleIntegerProperty(credits);
            this.grade   = new SimpleStringProperty(grade);
            this.point   = new SimpleDoubleProperty(point);
            this.major   = new SimpleBooleanProperty(major);
        }
        public String  getSubject() { return subject.get(); }
        public void    setSubject(String v) { subject.set(v); }
        public int     getCredits() { return credits.get(); }
        public void    setCredits(int v) { credits.set(v); }
        public String  getGrade() { return grade.get(); }
        public void    setGrade(String v) { grade.set(v); }
        public double  getPoint() { return point.get(); }
        public void    setPoint(double v) { point.set(v); }
        public boolean isMajor() { return major.get(); }
        public void    setMajor(boolean v) { major.set(v); }
    }

    private void applyStyles() {
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }
}
