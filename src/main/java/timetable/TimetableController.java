package timetable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;

import common.model.Subject;
import common.model.Assignment;
import common.model.Exam;
import common.database.DatabaseManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 완전한 시간표 컨트롤러 - UI와 기능 모두 포함
 */
public class TimetableController extends VBox {
    
    private GridPane timetableGrid;
    private TableView<SubjectTableItem> subjectTable;
    private TextField subjectField;
    private TextField professorField;
    private TextField locationField;
    private ComboBox<String> dayComboBox;
    private ComboBox<String> startTimeComboBox;
    private ComboBox<String> endTimeComboBox;
    private TextField creditsField;
    private ComboBox<String> categoryComboBox;
    
    private ObservableList<SubjectTableItem> subjectData;
    private DatabaseManager dbManager;
    
    private static final String[] DAYS = {"월", "화", "수", "목", "금", "토", "일"};
    private static final String[] TIME_SLOTS = {
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "13:00", "13:30", "14:00", "14:30",
        "15:00", "15:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00", "19:30", "20:00", "20:30"
    };
    
    public TimetableController() {
        this.dbManager = DatabaseManager.getInstance();
        this.subjectData = FXCollections.observableArrayList();
        
        initializeComponents();
        setupLayout();
        loadSubjects();
        refreshTimetableGrid();
    }
    
    private void initializeComponents() {
        setupTimetableGrid();
        
        subjectField = new TextField();
        subjectField.setPromptText("과목명");
        
        professorField = new TextField();
        professorField.setPromptText("교수님");
        
        locationField = new TextField();
        locationField.setPromptText("강의실");
        
        creditsField = new TextField();
        creditsField.setPromptText("학점");
        
        dayComboBox = new ComboBox<>();
        dayComboBox.getItems().addAll(DAYS);
        dayComboBox.setPromptText("요일 선택");
        
        startTimeComboBox = new ComboBox<>();
        startTimeComboBox.getItems().addAll(TIME_SLOTS);
        startTimeComboBox.setPromptText("시작시간");
        
        endTimeComboBox = new ComboBox<>();
        endTimeComboBox.getItems().addAll(TIME_SLOTS);
        endTimeComboBox.setPromptText("종료시간");
        
        categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("전공필수", "전공선택", "교양", "자유선택");
        categoryComboBox.setValue("전공필수");
        
        setupSubjectTable();
    }
    
    private void setupTimetableGrid() {
        timetableGrid = new GridPane();
        timetableGrid.setHgap(2);
        timetableGrid.setVgap(2);
        timetableGrid.setPadding(new Insets(10));
        timetableGrid.setStyle("-fx-background-color: white; -fx-border-color: #ddd;");
        
        // 헤더 생성
        Label timeHeader = new Label("시간");
        timeHeader.setAlignment(Pos.CENTER);
        timeHeader.setPrefSize(80, 40);
        timeHeader.setStyle("-fx-background-color: #E9ECEF; -fx-border-color: #DEE2E6; -fx-font-weight: bold;");
        timetableGrid.add(timeHeader, 0, 0);
        
        for (int i = 0; i < DAYS.length; i++) {
            Label dayLabel = new Label(DAYS[i]);
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefSize(120, 40);
            dayLabel.setStyle("-fx-background-color: #E9ECEF; -fx-border-color: #DEE2E6; -fx-font-weight: bold;");
            timetableGrid.add(dayLabel, i + 1, 0);
        }
        
        // 시간별 행과 셀 생성
        for (int hour = 0; hour < TIME_SLOTS.length; hour++) {
            Label timeLabel = new Label(TIME_SLOTS[hour]);
            timeLabel.setAlignment(Pos.CENTER);
            timeLabel.setPrefSize(80, 30);
            timeLabel.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #DEE2E6;");
            timetableGrid.add(timeLabel, 0, hour + 1);
            
            for (int day = 0; day < DAYS.length; day++) {
                Button cellButton = new Button();
                cellButton.setPrefSize(120, 30);
                cellButton.setStyle("-fx-background-color: white; -fx-border-color: #DEE2E6;");
                
                final int finalDay = day;
                final int finalHour = hour;
                cellButton.setOnAction(e -> handleCellClick(finalDay, finalHour));
                
                timetableGrid.add(cellButton, day + 1, hour + 1);
            }
        }
    }
    
    private void setupSubjectTable() {
        subjectTable = new TableView<>();
        subjectTable.setItems(subjectData);
        
        TableColumn<SubjectTableItem, String> nameCol = new TableColumn<>("과목명");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<SubjectTableItem, String> professorCol = new TableColumn<>("교수");
        professorCol.setCellValueFactory(new PropertyValueFactory<>("professor"));
        professorCol.setPrefWidth(100);
        
        TableColumn<SubjectTableItem, String> timeCol = new TableColumn<>("시간");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("timeInfo"));
        timeCol.setPrefWidth(150);
        
        TableColumn<SubjectTableItem, String> locationCol = new TableColumn<>("강의실");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(100);
        
        TableColumn<SubjectTableItem, Integer> creditsCol = new TableColumn<>("학점");
        creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
        creditsCol.setPrefWidth(60);
        
        subjectTable.getColumns().addAll(nameCol, professorCol, timeCol, locationCol, creditsCol);
        subjectTable.setPrefHeight(200);
        
        subjectTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadSubjectToForm(newSelection);
                }
            }
        );
    }
    
    private void setupLayout() {
        Label titleLabel = new Label("📅 시간표 관리");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #212529;");
        
        HBox mainContent = new HBox(20);
        
        // 왼쪽: 시간표 그리드
        VBox leftPane = new VBox(15);
        leftPane.setPrefWidth(900);
        
        Label timetableTitle = new Label("🗓️ 주간 시간표");
        timetableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        ScrollPane timetableScroll = new ScrollPane(timetableGrid);
        timetableScroll.setPrefHeight(500);
        timetableScroll.setFitToWidth(true);
        
        leftPane.getChildren().addAll(timetableTitle, timetableScroll);
        
        // 오른쪽: 입력 폼과 과목 목록
        VBox rightPane = new VBox(15);
        rightPane.setPrefWidth(400);
        
        // 입력 폼
        VBox formContainer = new VBox(10);
        formContainer.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label formTitle = new Label("➕ 과목 추가/수정");
        formTitle.setStyle("-fx-font-weight: bold;");
        
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        formGrid.add(new Label("과목명:"), 0, 0);
        formGrid.add(subjectField, 1, 0);
        formGrid.add(new Label("교수님:"), 0, 1);
        formGrid.add(professorField, 1, 1);
        formGrid.add(new Label("강의실:"), 0, 2);
        formGrid.add(locationField, 1, 2);
        formGrid.add(new Label("학점:"), 0, 3);
        formGrid.add(creditsField, 1, 3);
        formGrid.add(new Label("분류:"), 0, 4);
        formGrid.add(categoryComboBox, 1, 4);
        formGrid.add(new Label("요일:"), 0, 5);
        formGrid.add(dayComboBox, 1, 5);
        formGrid.add(new Label("시작시간:"), 0, 6);
        formGrid.add(startTimeComboBox, 1, 6);
        formGrid.add(new Label("종료시간:"), 0, 7);
        formGrid.add(endTimeComboBox, 1, 7);
        
        // 버튼 영역
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("추가");
        addButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white; -fx-font-weight: bold;");
        addButton.setOnAction(e -> addSubject());
        
        Button editButton = new Button("수정");
        editButton.setStyle("-fx-background-color: #6C757D; -fx-text-fill: white;");
        editButton.setOnAction(e -> editSubject());
        
        Button deleteButton = new Button("삭제");
        deleteButton.setStyle("-fx-background-color: #DC3545; -fx-text-fill: white;");
        deleteButton.setOnAction(e -> deleteSubject());
        
        Button clearButton = new Button("초기화");
        clearButton.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
        clearButton.setOnAction(e -> clearForm());
        
        buttonBox.getChildren().addAll(addButton, editButton, deleteButton, clearButton);
        
        formContainer.getChildren().addAll(formTitle, formGrid, buttonBox);
        
        // 과목 목록 테이블
        VBox tableContainer = new VBox(10);
        tableContainer.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label tableTitle = new Label("📚 수강 과목 목록");
        tableTitle.setStyle("-fx-font-weight: bold;");
        
        HBox subjectButtonBox = new HBox(10);
        Button viewAssignmentsBtn = new Button("과제 보기");
        viewAssignmentsBtn.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white;");
        viewAssignmentsBtn.setOnAction(e -> viewSubjectAssignments());
        
        Button viewExamsBtn = new Button("시험 보기");
        viewExamsBtn.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");
        viewExamsBtn.setOnAction(e -> viewSubjectExams());
        
        subjectButtonBox.getChildren().addAll(viewAssignmentsBtn, viewExamsBtn);
        
        tableContainer.getChildren().addAll(tableTitle, subjectTable, subjectButtonBox);
        
        rightPane.getChildren().addAll(formContainer, tableContainer);
        
        mainContent.getChildren().addAll(leftPane, rightPane);
        this.getChildren().addAll(titleLabel, mainContent);
    }
    
    private void handleCellClick(int day, int hour) {
        String dayName = DAYS[day];
        String time = TIME_SLOTS[hour];
        
        Optional<Subject> existingSubject = findSubjectAtTime(dayName, time);
        
        if (existingSubject.isPresent()) {
            showSubjectDetails(existingSubject.get());
        } else {
            dayComboBox.setValue(dayName);
            startTimeComboBox.setValue(time);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("시간표");
            alert.setHeaderText(dayName + " " + time);
            alert.setContentText("이 시간에는 수업이 없습니다.\n오른쪽 폼에서 새 수업을 추가할 수 있습니다.");
            alert.showAndWait();
        }
    }
    
    private Optional<Subject> findSubjectAtTime(String day, String time) {
        List<Subject> subjects = dbManager.getAllSubjects();
        return subjects.stream()
                .filter(s -> day.equals(s.getDayOfWeek()) && 
                           isTimeInRange(time, s.getStartTime(), s.getEndTime()))
                .findFirst();
    }
    
    private boolean isTimeInRange(String time, String startTime, String endTime) {
        if (startTime == null || endTime == null) return false;
        return time.compareTo(startTime) >= 0 && time.compareTo(endTime) < 0;
    }
    
    private void showSubjectDetails(Subject subject) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("수업 정보 - " + subject.getName());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label info = new Label(String.format(
            "📚 과목: %s\n" +
            "👨‍🏫 교수: %s\n" +
            "🏫 강의실: %s\n" +
            "⏰ 시간: %s %s ~ %s\n" +
            "📊 학점: %d\n" +
            "📂 분류: %s",
            subject.getName(),
            subject.getProfessor() != null ? subject.getProfessor() : "미정",
            subject.getClassroom() != null ? subject.getClassroom() : "미정",
            subject.getDayOfWeek(),
            subject.getStartTime(),
            subject.getEndTime(),
            subject.getCredits(),
            subject.getCategory() != null ? subject.getCategory() : "미정"
        ));
        
        HBox buttonBox = new HBox(10);
        Button assignmentBtn = new Button("📝 과제 보기");
        assignmentBtn.setOnAction(e -> {
            dialog.close();
            viewSubjectAssignments(subject);
        });
        
        Button examBtn = new Button("📋 시험 보기");
        examBtn.setOnAction(e -> {
            dialog.close();
            viewSubjectExams(subject);
        });
        
        Button closeBtn = new Button("닫기");
        closeBtn.setOnAction(e -> dialog.close());
        
        buttonBox.getChildren().addAll(assignmentBtn, examBtn, closeBtn);
        content.getChildren().addAll(info, buttonBox);
        
        Scene scene = new Scene(content, 350, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void addSubject() {
        try {
            Subject newSubject = createSubjectFromForm();
            
            if (hasTimeConflict(newSubject)) {
                showAlert("시간 충돌", "같은 시간에 다른 수업이 있습니다!");
                return;
            }
            
            dbManager.addSubject(newSubject);
            loadSubjects();
            refreshTimetableGrid();
            clearForm();
            
            showAlert("성공", "과목이 추가되었습니다!");
            
        } catch (Exception e) {
            showAlert("오류", "과목 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private void editSubject() {
        SubjectTableItem selected = subjectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("선택 오류", "수정할 과목을 선택해주세요!");
            return;
        }
        
        try {
            Subject updatedSubject = createSubjectFromForm();
            updatedSubject.setId(selected.getId());
            
            if (hasTimeConflictExcept(updatedSubject, selected.getId())) {
                showAlert("시간 충돌", "같은 시간에 다른 수업이 있습니다!");
                return;
            }
            
            dbManager.updateSubject(updatedSubject);
            loadSubjects();
            refreshTimetableGrid();
            clearForm();
            
            showAlert("성공", "과목이 수정되었습니다!");
            
        } catch (Exception e) {
            showAlert("오류", "과목 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    private void deleteSubject() {
        SubjectTableItem selected = subjectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("선택 오류", "삭제할 과목을 선택해주세요!");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("삭제 확인");
        confirmation.setHeaderText("과목 삭제");
        confirmation.setContentText("정말로 '" + selected.getName() + "' 과목을 삭제하시겠습니까?\n관련된 과제와 시험도 함께 삭제됩니다.");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbManager.deleteSubject(selected.getId());
            loadSubjects();
            refreshTimetableGrid();
            clearForm();
            
            showAlert("성공", "과목이 삭제되었습니다!");
        }
    }
    
    private Subject createSubjectFromForm() {
        if (subjectField.getText().trim().isEmpty() ||
            dayComboBox.getValue() == null ||
            startTimeComboBox.getValue() == null ||
            endTimeComboBox.getValue() == null) {
            throw new IllegalArgumentException("필수 항목을 모두 입력해주세요!");
        }
        
        Subject subject = new Subject();
        subject.setName(subjectField.getText().trim());
        subject.setProfessor(professorField.getText().trim().isEmpty() ? null : professorField.getText().trim());
        subject.setClassroom(locationField.getText().trim().isEmpty() ? null : locationField.getText().trim());
        subject.setDayOfWeek(dayComboBox.getValue());
        subject.setStartTime(startTimeComboBox.getValue());
        subject.setEndTime(endTimeComboBox.getValue());
        subject.setCategory(categoryComboBox.getValue());
        
        try {
            subject.setCredits(creditsField.getText().trim().isEmpty() ? 3 : Integer.parseInt(creditsField.getText().trim()));
        } catch (NumberFormatException e) {
            subject.setCredits(3);
        }
        
        return subject;
    }
    
    private boolean hasTimeConflict(Subject newSubject) {
        return hasTimeConflictExcept(newSubject, -1);
    }
    
    private boolean hasTimeConflictExcept(Subject newSubject, int exceptId) {
        List<Subject> subjects = dbManager.getAllSubjects();
        
        return subjects.stream()
                .filter(s -> s.getId() != exceptId)
                .filter(s -> newSubject.getDayOfWeek().equals(s.getDayOfWeek()))
                .anyMatch(s -> timeOverlap(
                    newSubject.getStartTime(), newSubject.getEndTime(),
                    s.getStartTime(), s.getEndTime()
                ));
    }
    
    private boolean timeOverlap(String start1, String end1, String start2, String end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
    }
    
    private void loadSubjectToForm(SubjectTableItem item) {
        subjectField.setText(item.getName());
        professorField.setText(item.getProfessor() != null ? item.getProfessor() : "");
        locationField.setText(item.getLocation() != null ? item.getLocation() : "");
        creditsField.setText(String.valueOf(item.getCredits()));
        categoryComboBox.setValue(item.getCategory());
        dayComboBox.setValue(item.getDayOfWeek());
        startTimeComboBox.setValue(item.getStartTime());
        endTimeComboBox.setValue(item.getEndTime());
    }
    
    private void clearForm() {
        subjectField.clear();
        professorField.clear();
        locationField.clear();
        creditsField.clear();
        dayComboBox.setValue(null);
        startTimeComboBox.setValue(null);
        endTimeComboBox.setValue(null);
        categoryComboBox.setValue("전공필수");
        subjectTable.getSelectionModel().clearSelection();
    }
    
    private void loadSubjects() {
        subjectData.clear();
        List<Subject> subjects = dbManager.getAllSubjects();
        
        for (Subject subject : subjects) {
            subjectData.add(new SubjectTableItem(subject));
        }
    }
    
    private void refreshTimetableGrid() {
        // 모든 셀 초기화
        for (int day = 0; day < DAYS.length; day++) {
            for (int hour = 0; hour < TIME_SLOTS.length; hour++) {
                Button cell = (Button) getNodeFromGridPane(timetableGrid, day + 1, hour + 1);
                if (cell != null) {
                    cell.setText("");
                    cell.setStyle("-fx-background-color: white; -fx-border-color: #DEE2E6;");
                }
            }
        }
        
        // 과목 정보로 셀 채우기
        List<Subject> subjects = dbManager.getAllSubjects();
        for (Subject subject : subjects) {
            addSubjectToGrid(subject);
        }
    }
    
    private void addSubjectToGrid(Subject subject) {
        if (subject.getDayOfWeek() == null || subject.getStartTime() == null || subject.getEndTime() == null) {
            return;
        }
        
        int dayIndex = getDayIndex(subject.getDayOfWeek());
        int startTimeIndex = getTimeIndex(subject.getStartTime());
        int endTimeIndex = getTimeIndex(subject.getEndTime());
        
        if (dayIndex == -1 || startTimeIndex == -1 || endTimeIndex == -1) {
            return;
        }
        
        for (int timeIndex = startTimeIndex; timeIndex < endTimeIndex; timeIndex++) {
            Button cell = (Button) getNodeFromGridPane(timetableGrid, dayIndex + 1, timeIndex + 1);
            if (cell != null) {
                if (timeIndex == startTimeIndex) {
                    cell.setText(subject.getName() + "\n(" + subject.getStartTime() + "~" + subject.getEndTime() + ")");
                } else {
                    cell.setText("↑");
                }
                cell.setStyle("-fx-background-color: lightblue; -fx-border-color: #007bff; -fx-text-fill: black; -fx-font-size: 10px;");
            }
        }
    }
    
    private int getDayIndex(String day) {
        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equals(day)) {
                return i;
            }
        }
        return -1;
    }
    
    private int getTimeIndex(String time) {
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            if (TIME_SLOTS[i].equals(time)) {
                return i;
            }
        }
        return -1;
    }
    
    private javafx.scene.Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (javafx.scene.Node node : gridPane.getChildren()) {
            Integer nodeCol = GridPane.getColumnIndex(node);
            Integer nodeRow = GridPane.getRowIndex(node);
            
            if (nodeCol != null && nodeRow != null && nodeCol == col && nodeRow == row) {
                return node;
            }
        }
        return null;
    }
    
    private void viewSubjectAssignments() {
        SubjectTableItem selected = subjectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("선택 오류", "과목을 선택해주세요!");
            return;
        }
        
        viewSubjectAssignments(dbManager.getSubjectById(selected.getId()).orElse(null));
    }
    
    private void viewSubjectAssignments(Subject subject) {
        if (subject == null) return;
        
        List<Assignment> assignments = dbManager.getAssignmentsBySubject(subject.getId());
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(subject.getName() + " - 과제 목록");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        if (assignments.isEmpty()) {
            Label noData = new Label("등록된 과제가 없습니다.");
            content.getChildren().add(noData);
        } else {
            TableView<Assignment> assignmentTable = new TableView<>();
            
            TableColumn<Assignment, String> titleCol = new TableColumn<>("제목");
            titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
            
            TableColumn<Assignment, String> dueDateCol = new TableColumn<>("마감일");
            dueDateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDueDate() != null ? data.getValue().getDueDate().toString() : "미정"));
            
            TableColumn<Assignment, String> statusCol = new TableColumn<>("상태");
            statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));
            
            assignmentTable.getColumns().addAll(titleCol, dueDateCol, statusCol);
            assignmentTable.getItems().addAll(assignments);
            assignmentTable.setPrefHeight(200);
            
            content.getChildren().add(assignmentTable);
        }
        
        Button closeBtn = new Button("닫기");
        closeBtn.setOnAction(e -> dialog.close());
        
        content.getChildren().add(closeBtn);
        
        Scene scene = new Scene(content, 500, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void viewSubjectExams() {
        SubjectTableItem selected = subjectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("선택 오류", "과목을 선택해주세요!");
            return;
        }
        
        viewSubjectExams(dbManager.getSubjectById(selected.getId()).orElse(null));
    }
    
    private void viewSubjectExams(Subject subject) {
        if (subject == null) return;
        
        List<Exam> exams = dbManager.getExamsBySubject(subject.getId());
        
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(subject.getName() + " - 시험 목록");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        if (exams.isEmpty()) {
            Label noData = new Label("등록된 시험이 없습니다.");
            content.getChildren().add(noData);
        } else {
            TableView<Exam> examTable = new TableView<>();
            
            TableColumn<Exam, String> titleCol = new TableColumn<>("시험명");
            titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTitle()));
            
            TableColumn<Exam, String> typeCol = new TableColumn<>("유형");
            typeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getType()));
            
            TableColumn<Exam, String> dateCol = new TableColumn<>("시험일시");
            dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getExamDateTime() != null ? data.getValue().getExamDateTime().toString() : "미정"));
            
            TableColumn<Exam, String> locationCol = new TableColumn<>("장소");
            locationCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getLocation() != null ? data.getValue().getLocation() : "미정"));
            
            examTable.getColumns().addAll(titleCol, typeCol, dateCol, locationCol);
            examTable.getItems().addAll(exams);
            examTable.setPrefHeight(200);
            
            content.getChildren().add(examTable);
        }
        
        Button closeBtn = new Button("닫기");
        closeBtn.setOnAction(e -> dialog.close());
        
        content.getChildren().add(closeBtn);
        
        Scene scene = new Scene(content, 600, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // 테이블 아이템 클래스
    public static class SubjectTableItem {
        private int id;
        private String name;
        private String professor;
        private String location;
        private int credits;
        private String category;
        private String dayOfWeek;
        private String startTime;
        private String endTime;
        private String timeInfo;
        
        public SubjectTableItem(Subject subject) {
            this.id = subject.getId();
            this.name = subject.getName();
            this.professor = subject.getProfessor();
            this.location = subject.getClassroom();
            this.credits = subject.getCredits();
            this.category = subject.getCategory();
            this.dayOfWeek = subject.getDayOfWeek();
            this.startTime = subject.getStartTime();
            this.endTime = subject.getEndTime();
            
            // 시간 정보 문자열 생성
            if (dayOfWeek != null && startTime != null && endTime != null) {
                this.timeInfo = dayOfWeek + " " + startTime + "~" + endTime;
            } else {
                this.timeInfo = "시간 미정";
            }
        }
        
        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public String getProfessor() { return professor; }
        public String getLocation() { return location; }
        public int getCredits() { return credits; }
        public String getCategory() { return category; }
        public String getDayOfWeek() { return dayOfWeek; }
        public String getStartTime() { return startTime; }
        public String getEndTime() { return endTime; }
        public String getTimeInfo() { return timeInfo; }
    }
}