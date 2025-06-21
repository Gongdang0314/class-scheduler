package ui.demo;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ui.UIStyleManager;

/**
 * UI 컴포넌트들의 디자인을 확인하기 위한 데모 클래스
 * 모든 스타일이 제대로 적용되는지 테스트할 수 있습니다.
 */
public class UIComponentDemo extends Application {

    @Override
    public void start(Stage primaryStage) {
        ScrollPane scrollPane = UIStyleManager.createStandardScrollPane(createDemoContent());
        
        Scene scene = new Scene(scrollPane, 1000, 700);
        scene.getStylesheets().add("styles.css");
        
        primaryStage.setTitle("UI 컴포넌트 데모 - 디자인 참고용");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createDemoContent() {
        VBox mainContainer = new VBox(UIStyleManager.STANDARD_SPACING);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getStyleClass().add("root");
        
        // 제목
        Label titleLabel = UIStyleManager.createTitleLabel("🎨 UI 컴포넌트 스타일 가이드");
        
        // 1. 버튼 섹션
        VBox buttonSection = createButtonSection();
        
        // 2. 입력 필드 섹션
        VBox inputSection = createInputSection();
        
        // 3. 테이블 섹션
        VBox tableSection = createTableSection();
        
        // 4. 차트 섹션
        VBox chartSection = createChartSection();
        
        // 5. 레이아웃 섹션
        VBox layoutSection = createLayoutSection();
        
        mainContainer.getChildren().addAll(
            titleLabel,
            buttonSection,
            inputSection,
            tableSection,
            chartSection,
            layoutSection
        );
        
        return mainContainer;
    }
    
    private VBox createButtonSection() {
        VBox container = UIStyleManager.createStandardContainer();
        
        Label sectionTitle = UIStyleManager.createTitleLabel("🔘 버튼 스타일");
        Label sectionDesc = UIStyleManager.createSubLabel("다양한 버튼 스타일을 확인할 수 있습니다.");
        
        HBox buttonBox = UIStyleManager.createStandardHBox();
        
        Button primaryBtn = UIStyleManager.createPrimaryButton("Primary 버튼");
        Button secondaryBtn = UIStyleManager.createSecondaryButton("Secondary 버튼");
        Button disabledBtn = UIStyleManager.createPrimaryButton("비활성 버튼");
        disabledBtn.setDisable(true);
        
        UIStyleManager.applyTooltip(primaryBtn, "주요 액션 버튼입니다");
        UIStyleManager.applyTooltip(secondaryBtn, "보조 액션 버튼입니다");
        
        buttonBox.getChildren().addAll(primaryBtn, secondaryBtn, disabledBtn);
        
        container.getChildren().addAll(sectionTitle, sectionDesc, buttonBox);
        return container;
    }
    
    private VBox createInputSection() {
        VBox container = UIStyleManager.createStandardContainer();
        
        Label sectionTitle = UIStyleManager.createTitleLabel("📝 입력 필드 스타일");
        Label sectionDesc = UIStyleManager.createSubLabel("텍스트 필드, 콤보박스, 날짜 선택기 등의 스타일입니다.");
        
        GridPane inputGrid = UIStyleManager.createFormGrid();
        
        TextField textField = UIStyleManager.createStandardTextField("텍스트를 입력하세요");
        ComboBox<String> comboBox = UIStyleManager.createStandardComboBox();
        comboBox.getItems().addAll("옵션 1", "옵션 2", "옵션 3");
        comboBox.setValue("옵션 1");
        
        DatePicker datePicker = UIStyleManager.createStandardDatePicker();
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("비밀번호");
        passwordField.getStyleClass().add("password-field");
        
        TextArea textArea = new TextArea();
        textArea.setPromptText("여러 줄 텍스트를 입력하세요...");
        textArea.setPrefRowCount(3);
        textArea.getStyleClass().add("text-field");
        
        inputGrid.add(new Label("텍스트 필드:"), 0, 0);
        inputGrid.add(textField, 1, 0);
        inputGrid.add(new Label("콤보박스:"), 0, 1);
        inputGrid.add(comboBox, 1, 1);
        inputGrid.add(new Label("날짜 선택:"), 0, 2);
        inputGrid.add(datePicker, 1, 2);
        inputGrid.add(new Label("비밀번호:"), 0, 3);
        inputGrid.add(passwordField, 1, 3);
        inputGrid.add(new Label("텍스트 영역:"), 0, 4);
        inputGrid.add(textArea, 1, 4);
        
        container.getChildren().addAll(sectionTitle, sectionDesc, inputGrid);
        return container;
    }
    
    private VBox createTableSection() {
        VBox container = UIStyleManager.createStandardContainer();
        
        Label sectionTitle = UIStyleManager.createTitleLabel("📊 테이블 스타일");
        Label sectionDesc = UIStyleManager.createSubLabel("데이터 테이블의 스타일을 확인할 수 있습니다.");
        
        TableView<DemoItem> table = UIStyleManager.createStandardTableView();
        
        TableColumn<DemoItem, String> nameCol = new TableColumn<>("이름");
        TableColumn<DemoItem, String> typeCol = new TableColumn<>("유형");
        TableColumn<DemoItem, String> statusCol = new TableColumn<>("상태");
        TableColumn<DemoItem, String> dateCol = new TableColumn<>("날짜");
        
        nameCol.setPrefWidth(150);
        typeCol.setPrefWidth(100);
        statusCol.setPrefWidth(100);
        dateCol.setPrefWidth(120);
        
        table.getColumns().addAll(nameCol, typeCol, statusCol, dateCol);
        
        // 샘플 데이터 추가
        table.setItems(FXCollections.observableArrayList(
            new DemoItem("자바 프로그래밍 과제", "과제", "진행중", "2024-06-25"),
            new DemoItem("데이터베이스 중간고사", "시험", "완료", "2024-06-20"),
            new DemoItem("웹 개발 프로젝트", "프로젝트", "계획중", "2024-07-01"),
            new DemoItem("운영체제 발표", "발표", "준비중", "2024-06-28")
        ));
        
        table.setPrefHeight(150);
        
        container.getChildren().addAll(sectionTitle, sectionDesc, table);
        return container;
    }
    
    private VBox createChartSection() {
        VBox container = UIStyleManager.createStandardContainer();
        
        Label sectionTitle = UIStyleManager.createTitleLabel("📈 차트 스타일");
        Label sectionDesc = UIStyleManager.createSubLabel("차트와 그래프의 스타일을 확인할 수 있습니다.");
        
        // 파이 차트 예제
        PieChart pieChart = new PieChart();
        pieChart.getData().addAll(
            new PieChart.Data("A학점", 30),
            new PieChart.Data("B학점", 40),
            new PieChart.Data("C학점", 20),
            new PieChart.Data("기타", 10)
        );
        pieChart.setTitle("학점 분포 예시");
        pieChart.getStyleClass().add("chart");
        pieChart.setPrefHeight(250);
        
        // 진행률 바 예제
        HBox progressSection = UIStyleManager.createStandardHBox();
        Label progressLabel = new Label("전체 진행률:");
        ProgressBar progressBar = new ProgressBar(0.65);
        progressBar.setPrefWidth(200);
        Label percentLabel = new Label("65%");
        progressSection.getChildren().addAll(progressLabel, progressBar, percentLabel);
        
        container.getChildren().addAll(sectionTitle, sectionDesc, pieChart, progressSection);
        return container;
    }
    
    private VBox createLayoutSection() {
        VBox container = UIStyleManager.createStandardContainer();
        
        Label sectionTitle = UIStyleManager.createTitleLabel("📐 레이아웃 스타일");
        Label sectionDesc = UIStyleManager.createSubLabel("다양한 레이아웃 컨테이너의 스타일입니다.");
        
        // 카드 스타일 예제
        HBox cardBox = new HBox(UIStyleManager.STANDARD_SPACING);
        
        for (int i = 1; i <= 3; i++) {
            VBox card = UIStyleManager.createStandardContainer();
            card.setPrefWidth(200);
            
            Label cardTitle = UIStyleManager.createTitleLabel("카드 " + i);
            Label cardContent = UIStyleManager.createSubLabel("이것은 카드 스타일의 예제입니다. 그림자와 둥근 모서리가 적용되어 있습니다.");
            Button cardButton = UIStyleManager.createPrimaryButton("액션");
            
            card.getChildren().addAll(cardTitle, cardContent, cardButton);
            cardBox.getChildren().add(card);
        }
        
        // 라벨 스타일 예제
        VBox labelSection = UIStyleManager.createStandardContainer();
        labelSection.getChildren().addAll(
            UIStyleManager.createTitleLabel("제목 라벨 스타일"),
            UIStyleManager.createSubLabel("부제목 라벨 스타일"),
            UIStyleManager.createErrorLabel("에러 메시지 라벨 스타일")
        );
        
        container.getChildren().addAll(sectionTitle, sectionDesc, cardBox, labelSection);
        return container;
    }
    
    // 데모용 데이터 클래스
    public static class DemoItem {
        private String name;
        private String type;
        private String status;
        private String date;
        
        public DemoItem(String name, String type, String status, String date) {
            this.name = name;
            this.type = type;
            this.status = status;
            this.date = date;
        }
        
        public String getName() { return name; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public String getDate() { return date; }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}