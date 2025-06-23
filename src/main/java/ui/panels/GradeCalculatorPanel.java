// src/main/java/ui/panels/GradeCalculatorPanel.java
package ui.panels;

import java.util.List;
import java.util.stream.Collectors;

import common.database.DatabaseManager;
import common.model.Subject;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import ui.UIStyleManager;

public class GradeCalculatorPanel extends VBox {

    private DatabaseManager dbManager;

    // --- 상단 입력부
    private TextField        subjectField;
    private ComboBox<String> gradeComboBox;    // 평점 입력용 콤보박스 (A+~F, P, U)

    private Button addCreditButton;            // "학점추가"
    private Button deleteCreditButton;         // "학점삭제"

    // --- 테이블 & 차트
    private TableView<GradeItem>    gradeTable;
    private CategoryAxis            xAxis;
    private NumberAxis              yAxis;
    private BarChart<String, Number> distributionChart;

    public GradeCalculatorPanel() {
        dbManager = DatabaseManager.getInstance();

        initializeFields();
        initButtons();
        initTable();
        loadSubjects();
        initChart();
        layoutComponents();
        hookHandlers();

        // 스타일
        this.setPadding(new Insets(20));
        this.getStyleClass().add("root");
    }

    private void initializeFields() {
        subjectField  = UIStyleManager.createStandardTextField("과목명 입력");
        gradeComboBox = UIStyleManager.createStandardComboBox();
        gradeComboBox.getItems().addAll(
            "A+","A","B+","B","C+","C","D+","D","F","P","U"
        );

        gradeTable    = new TableView<>();
    }

    private void initButtons() {
        addCreditButton    = UIStyleManager.createPrimaryButton("학점추가");
        deleteCreditButton = UIStyleManager.createSecondaryButton("학점삭제");
    }

    private void initTable() {
        TableColumn<GradeItem, String>  colName   = new TableColumn<>("과목명");
        TableColumn<GradeItem, Integer> colCredit = new TableColumn<>("학점");
        TableColumn<GradeItem, String>  colGrade  = new TableColumn<>("성적");
        TableColumn<GradeItem, Double>  colPoint  = new TableColumn<>("평점");
        TableColumn<GradeItem, String>  colMajor  = new TableColumn<>("전공");  // MODIFIED

        colName  .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSubject()));
        colCredit.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCredits()).asObject());
        colGrade .setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGrade()));
        colPoint .setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPoint()).asObject());
        // 전공 여부에 따라 "O" 또는 "X" 표시
        colMajor .setCellValueFactory(c ->
            new SimpleStringProperty(c.getValue().isMajor() ? "O" : "X")
        );

        gradeTable.getColumns().addAll(colName, colCredit, colGrade, colPoint, colMajor);
        gradeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /** DB에서 시간표 과목 불러와 테이블에 세팅 */
    private void loadSubjects() {
        List<Subject> subjects = dbManager.getAllSubjects();
        List<GradeItem> items = subjects.stream()
            .map(s -> new GradeItem(
                s.getName(),
                s.getCredits(),
                "",                   // 초기 성적 없음
                0.0,                  // 초기 평점 0.0
                isMajorCategory(s.getCategory())
            ))
            .collect(Collectors.toList());
        gradeTable.getItems().setAll(items);
    }

    /** Subject.category가 "전공필수" 또는 "전공선택"인지 체크 */
    private boolean isMajorCategory(String category) {
        return "전공필수".equals(category) || "전공선택".equals(category);
    }

    private void initChart() {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        distributionChart = new BarChart<>(xAxis, yAxis);
        distributionChart.setTitle("과목별 평점 분포");
        distributionChart.setLegendSide(Side.BOTTOM);
        distributionChart.setAnimated(false);
    }

    private void layoutComponents() {
        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);
        inputGrid.setPadding(new Insets(0, 0, 10, 0));

        inputGrid.add(new Label("과목명"),    0, 0);
        inputGrid.add(subjectField,         1, 0);
        inputGrid.add(new Label("평점"),      2, 0);
        inputGrid.add(gradeComboBox,        3, 0);
        inputGrid.add(addCreditButton,      4, 0);
        inputGrid.add(deleteCreditButton,   5, 0);

        VBox leftBox = new VBox(10, inputGrid, gradeTable);
        leftBox.setPadding(new Insets(0, 0, 20, 0));

        this.getChildren().addAll(leftBox, distributionChart);
    }

    private void hookHandlers() {
        addCreditButton.setOnAction(e -> {
            String subj   = subjectField.getText().trim();
            String letter = gradeComboBox.getValue();
            if (subj.isEmpty() || letter == null) {
                System.err.println("❌ 과목명과 평점을 모두 입력해주세요.");
                return;
            }
            boolean updated = false;
            for (GradeItem item : gradeTable.getItems()) {
                if (item.getSubject().equals(subj)) {
                    item.setGrade(letter);
                    item.setPoint(mapGradeToPoint(letter));
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                System.err.println("❌ 해당 과목을 찾을 수 없습니다: " + subj);
            }
            gradeTable.refresh();
            resetChart();
        });

        deleteCreditButton.setOnAction(e -> {
            String subj = subjectField.getText().trim();
            if (subj.isEmpty()) {
                System.err.println("❌ 과목명을 입력해주세요.");
                return;
            }
            boolean removed = false;
            for (GradeItem item : gradeTable.getItems()) {
                if (item.getSubject().equals(subj)) {
                    item.setGrade("");
                    item.setPoint(0.0);
                    removed = true;
                    break;
                }
            }
            if (!removed) {
                System.err.println("❌ 해당 과목을 찾을 수 없습니다: " + subj);
            }
            gradeTable.refresh();
            resetChart();
        });
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
    private void resetChart() {
        distributionChart.getData().clear();
        for (GradeItem item : gradeTable.getItems()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(item.getSubject());
            series.getData().add(new XYChart.Data<>(item.getSubject(), item.getPoint()));
            distributionChart.getData().add(series);
        }
    }

    /** TableView용 데이터 모델 */
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
        public int     getCredits() { return credits.get(); }
        public String  getGrade()   { return grade.get(); }
        public double  getPoint()   { return point.get(); }
        public boolean isMajor()    { return major.get(); }

        public void setGrade(String g)    { grade.set(g); }
        public void setPoint(double p)    { point.set(p); }
        public void setCredits(int c)     { credits.set(c); }
        public void setMajor(boolean m)   { major.set(m); }
    }
}
