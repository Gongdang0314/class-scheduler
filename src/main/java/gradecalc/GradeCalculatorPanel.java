package gradecalc;

import common.model.Grade;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class GradeCalculatorPanel extends VBox {

    private TextField subjectField;
    private ComboBox<String> letterGradeBox;
    private ComboBox<Double> gpaBox;
    private Spinner<Integer> creditSpinner;
    private CheckBox majorCheck;
    private Button addButton;
    private Label resultLabel;

    private List<Grade> gradeList = new ArrayList<>();
    private GradeCalculator calculator = new GradeCalculator();
    double overallGPA = calculator.calculateGPA(gradeList);
    
    public GradeCalculatorPanel() {
        setPadding(new Insets(20));
        setSpacing(15);

        Label title = new Label("📚 학점 계산기");
        title.setFont(new Font(18));

        subjectField = new TextField();
        letterGradeBox = new ComboBox<>();
        gpaBox = new ComboBox<>();
        creditSpinner = new Spinner<>(1, 5, 3);
        majorCheck = new CheckBox("전공 과목 여부");

        letterGradeBox.getItems().addAll("A+", "A0", "B+", "B0", "C+", "C0", "D", "F");
        gpaBox.getItems().addAll(4.5, 4.0, 3.5, 3.0, 2.5, 2.0, 1.0, 0.0);

        addButton = new Button("과목 추가");
        resultLabel = new Label("GPA 결과: -");

        GridPane inputGrid = new GridPane();
        inputGrid.setHgap(10);
        inputGrid.setVgap(10);

        inputGrid.add(new Label("과목명:"), 0, 0);
        inputGrid.add(subjectField, 1, 0);
        inputGrid.add(new Label("등급:"), 0, 1);
        inputGrid.add(letterGradeBox, 1, 1);
        inputGrid.add(new Label("GPA:"), 0, 2);
        inputGrid.add(gpaBox, 1, 2);
        inputGrid.add(new Label("학점 수:"), 0, 3);
        inputGrid.add(creditSpinner, 1, 3);
        inputGrid.add(majorCheck, 1, 4);

        getChildren().addAll(title, inputGrid, addButton, resultLabel);

        addButton.setOnAction(e -> handleAddGrade());
    }

    private void handleAddGrade() {
        String subject = subjectField.getText();
        String letter = letterGradeBox.getValue();
        Double gpa = gpaBox.getValue();
        int credit = creditSpinner.getValue();
        boolean isMajor = majorCheck.isSelected();

        if (subject.isEmpty() || letter == null || gpa == null) {
            resultLabel.setText("❗ 모든 항목을 입력하세요.");
            return;
        }

        Grade g = new Grade(subject, letter, gpa, credit, isMajor);  // 생성자 필요!
        gradeList.add(g);

        double overallGPA = calculator.calculateGPA(gradeList);
        resultLabel.setText("누적 GPA: " + String.format("%.2f", overallGPA));
    }
}
