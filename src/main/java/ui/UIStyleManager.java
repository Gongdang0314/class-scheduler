package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * UI 스타일 적용을 위한 유틸리티 클래스
 * 공통 스타일과 레이아웃을 관리합니다.
 */
public class UIStyleManager {
    
    // 공통 여백 상수
    public static final Insets STANDARD_PADDING = new Insets(15);
    public static final Insets SMALL_PADDING = new Insets(10);
    public static final double STANDARD_SPACING = 15.0;
    public static final double SMALL_SPACING = 10.0;
    
    /**
     * 기본 컨테이너 스타일 적용
     */
    public static VBox createStandardContainer() {
        VBox container = new VBox(STANDARD_SPACING);
        container.getStyleClass().add("container");
        container.setPadding(STANDARD_PADDING);
        return container;
    }
    
    public static HBox createStandardHBox() {
        HBox hbox = new HBox(SMALL_SPACING);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(SMALL_PADDING);
        return hbox;
    }
    
    public static GridPane createStandardGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(SMALL_SPACING);
        gridPane.setVgap(SMALL_SPACING);
        gridPane.setPadding(STANDARD_PADDING);
        gridPane.getStyleClass().add("container");
        return gridPane;
    }
    
    /**
     * 버튼 스타일 적용
     */
    public static Button createPrimaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        return button;
    }
    
    public static Button createSecondaryButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().addAll("button", "button-secondary");
        return button;
    }
    
    /**
     * 라벨 스타일 적용
     */
    public static Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-title");
        return label;
    }
    
    public static Label createSubLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-sub");
        return label;
    }
    
    public static Label createErrorLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("label-error");
        return label;
    }
    
    /**
     * 테이블뷰 스타일 적용
     */
    public static <T> TableView<T> createStandardTableView() {
        TableView<T> tableView = new TableView<>();
        tableView.getStyleClass().add("table-view");
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tableView;
    }
    
    /**
     * 입력 필드 스타일 적용
     */
    public static TextField createStandardTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.getStyleClass().add("text-field");
        return textField;
    }
    
    public static ComboBox<String> createStandardComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getStyleClass().add("combo-box");
        return comboBox;
    }
    
    public static DatePicker createStandardDatePicker() {
        DatePicker datePicker = new DatePicker();
        datePicker.getStyleClass().add("date-picker");
        return datePicker;
    }
    
    /**
     * 스크롤 패널 생성
     */
    public static ScrollPane createStandardScrollPane(Node content) {
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }
    
    /**
     * 폼 그리드 생성 (라벨-입력필드 쌍으로 구성)
     */
    public static GridPane createFormGrid() {
        GridPane grid = createStandardGridPane();
        
        // 컬럼 제약 설정 (라벨 30%, 입력필드 70%)
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(30);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        
        grid.getColumnConstraints().addAll(col1, col2);
        return grid;
    }
    
    /**
     * 툴팁 적용
     */
    public static void applyTooltip(Node node, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.getStyleClass().add("tooltip");
        Tooltip.install(node, tooltip);
    }
    
    /**
     * 노드에 표준 여백 적용
     */
    public static void applyStandardMargin(Node node) {
        if (node instanceof Region) {
            Region region = (Region) node;
            region.setPadding(STANDARD_PADDING);
        }
    }
}