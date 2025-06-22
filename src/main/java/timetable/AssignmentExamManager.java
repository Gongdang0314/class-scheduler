package timetable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import common.database.DatabaseManager;
import common.model.Assignment;
import common.model.Exam;
import common.model.Subject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 과제/시험 관리 기능을 제공하는 클래스
 * 시간표에서 과목을 선택했을 때 과제/시험을 추가하고 관리할 수 있습니다.
 */
public class AssignmentExamManager {
    
    private DatabaseManager dbManager;
    
    public AssignmentExamManager() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * 과제 추가 다이얼로그를 열기
     */
    public void showAddAssignmentDialog(Subject subject) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("과제 추가 - " + (subject != null ? subject.getName() : "전체"));
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // 입력 폼
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        TextField titleField = new TextField();
        titleField.setPromptText("과제 제목");
        
        ComboBox<Subject> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(dbManager.getAllSubjects());
        subjectCombo.setConverter(new javafx.util.StringConverter<Subject>() {
            @Override
            public String toString(Subject subject) {
                return subject != null ? subject.getName() : "";
            }
            
            @Override
            public Subject fromString(String string) {
                return null;
            }
        });
        
        if (subject != null) {
            subjectCombo.setValue(subject);
            subjectCombo.setDisable(true);
        }
        
        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setValue(LocalDate.now().plusDays(7)); // 기본 일주일 후
        
        ComboBox<String> priorityCombo = new ComboBox<>();
        priorityCombo.getItems().addAll("낮음", "보통", "높음", "매우높음");
        priorityCombo.setValue("보통");
        
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("미완료", "진행중", "완료");
        statusCombo.setValue("미완료");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("과제 설명 (선택사항)");
        descriptionArea.setPrefRowCount(3);
        
        formGrid.add(new Label("제목:"), 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(new Label("과목:"), 0, 1);
        formGrid.add(subjectCombo, 1, 1);
        formGrid.add(new Label("마감일:"), 0, 2);
        formGrid.add(dueDatePicker, 1, 2);
        formGrid.add(new Label("우선순위:"), 0, 3);
        formGrid.add(priorityCombo, 1, 3);
        formGrid.add(new Label("상태:"), 0, 4);
        formGrid.add(statusCombo, 1, 4);
        formGrid.add(new Label("설명:"), 0, 5);
        formGrid.add(descriptionArea, 1, 5);
        
        // 버튼
        HBox buttonBox = new HBox(10);
        Button saveButton = new Button("저장");
        saveButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                Assignment assignment = new Assignment();
                
                if (titleField.getText().trim().isEmpty()) {
                    showAlert("입력 오류", "제목을 입력해주세요!");
                    return;
                }
                
                if (subjectCombo.getValue() == null) {
                    showAlert("입력 오류", "과목을 선택해주세요!");
                    return;
                }
                
                assignment.setTitle(titleField.getText().trim());
                assignment.setSubjectId(subjectCombo.getValue().getId());
                assignment.setDueDate(dueDatePicker.getValue());
                assignment.setPriority(priorityCombo.getValue());
                assignment.setStatus(statusCombo.getValue());
                assignment.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());
                
                dbManager.addAssignment(assignment);
                showAlert("성공", "과제가 추가되었습니다!");
                dialog.close();
                
            } catch (Exception ex) {
                showAlert("오류", "과제 추가 중 오류가 발생했습니다: " + ex.getMessage());
            }
        });
        
        Button cancelButton = new Button("취소");
        cancelButton.setOnAction(e -> dialog.close());
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        content.getChildren().addAll(formGrid, buttonBox);
        
        Scene scene = new Scene(content, 400, 450);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    /**
     * 시험 추가 다이얼로그를 열기
     */
    public void showAddExamDialog(Subject subject) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("시험 추가 - " + (subject != null ? subject.getName() : "전체"));
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // 입력 폼
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        
        TextField titleField = new TextField();
        titleField.setPromptText("시험명");
        
        ComboBox<Subject> subjectCombo = new ComboBox<>();
        subjectCombo.getItems().addAll(dbManager.getAllSubjects());
        subjectCombo.setConverter(new javafx.util.StringConverter<Subject>() {
            @Override
            public String toString(Subject subject) {
                return subject != null ? subject.getName() : "";
            }
            
            @Override
            public Subject fromString(String string) {
                return null;
            }
        });
        
        if (subject != null) {
            subjectCombo.setValue(subject);
            subjectCombo.setDisable(true);
        }
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("중간고사", "기말고사", "쪽지시험", "실기시험", "발표");
        typeCombo.setValue("중간고사");
        
        DatePicker examDatePicker = new DatePicker();
        examDatePicker.setValue(LocalDate.now().plusDays(14)); // 기본 2주 후
        
        ComboBox<String> examHourCombo = new ComboBox<>();
        for (int i = 9; i <= 21; i++) {
            examHourCombo.getItems().add(String.format("%02d", i));
        }
        examHourCombo.setValue("10");
        
        ComboBox<String> examMinuteCombo = new ComboBox<>();
        examMinuteCombo.getItems().addAll("00", "30");
        examMinuteCombo.setValue("00");
        
        TextField locationField = new TextField();
        locationField.setPromptText("시험 장소 (선택사항)");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("시험 범위/설명 (선택사항)");
        descriptionArea.setPrefRowCount(3);
        
        HBox timeBox = new HBox(5);
        timeBox.getChildren().addAll(examHourCombo, new Label(":"), examMinuteCombo);
        
        formGrid.add(new Label("시험명:"), 0, 0);
        formGrid.add(titleField, 1, 0);
        formGrid.add(new Label("과목:"), 0, 1);
        formGrid.add(subjectCombo, 1, 1);
        formGrid.add(new Label("유형:"), 0, 2);
        formGrid.add(typeCombo, 1, 2);
        formGrid.add(new Label("시험일:"), 0, 3);
        formGrid.add(examDatePicker, 1, 3);
        formGrid.add(new Label("시험시간:"), 0, 4);
        formGrid.add(timeBox, 1, 4);
        formGrid.add(new Label("장소:"), 0, 5);
        formGrid.add(locationField, 1, 5);
        formGrid.add(new Label("설명:"), 0, 6);
        formGrid.add(descriptionArea, 1, 6);
        
        // 버튼
        HBox buttonBox = new HBox(10);
        Button saveButton = new Button("저장");
        saveButton.setStyle("-fx-background-color: #4A90E2; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            try {
                Exam exam = new Exam();
                
                if (titleField.getText().trim().isEmpty()) {
                    showAlert("입력 오류", "시험명을 입력해주세요!");
                    return;
                }
                
                if (subjectCombo.getValue() == null) {
                    showAlert("입력 오류", "과목을 선택해주세요!");
                    return;
                }
                
                if (examDatePicker.getValue() == null) {
                    showAlert("입력 오류", "시험일을 선택해주세요!");
                    return;
                }
                
                exam.setTitle(titleField.getText().trim());
                exam.setSubjectId(subjectCombo.getValue().getId());
                exam.setType(typeCombo.getValue());
                
                // 시험 날짜와 시간 결합
                LocalDateTime examDateTime = LocalDateTime.of(
                    examDatePicker.getValue(),
                    Integer.parseInt(examHourCombo.getValue()),
                    Integer.parseInt(examMinuteCombo.getValue())
                );
                exam.setExamDateTime(examDateTime);
                
                exam.setLocation(locationField.getText().trim().isEmpty() ? null : locationField.getText().trim());
                exam.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());
                
                dbManager.addExam(exam);
                showAlert("성공", "시험이 추가되었습니다!");
                dialog.close();
                
            } catch (Exception ex) {
                showAlert("오류", "시험 추가 중 오류가 발생했습니다: " + ex.getMessage());
            }
        });
        
        Button cancelButton = new Button("취소");
        cancelButton.setOnAction(e -> dialog.close());
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        content.getChildren().addAll(formGrid, buttonBox);
        
        Scene scene = new Scene(content, 400, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    /**
     * 과제/시험 통합 관리 다이얼로그
     */
    public void showTaskManagementDialog(Subject subject) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("과제/시험 관리" + (subject != null ? " - " + subject.getName() : ""));
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        // 탭 패널
        TabPane tabPane = new TabPane();
        
        // 과제 탭
        Tab assignmentTab = new Tab("📝 과제");
        assignmentTab.setClosable(false);
        VBox assignmentContent = createAssignmentTabContent(subject);
        assignmentTab.setContent(assignmentContent);
        
        // 시험 탭
        Tab examTab = new Tab("📋 시험");
        examTab.setClosable(false);
        VBox examContent = createExamTabContent(subject);
        examTab.setContent(examContent);
        
        tabPane.getTabs().addAll(assignmentTab, examTab);
        
        // 하단 버튼
        HBox buttonBox = new HBox(10);
        Button addAssignmentBtn = new Button("📝 과제 추가");
        addAssignmentBtn.setStyle("-fx-background-color: #17A2B8; -fx-text-fill: white;");
        addAssignmentBtn.setOnAction(e -> {
            showAddAssignmentDialog(subject);
            // 다이얼로그 새로고침
            dialog.close();
            showTaskManagementDialog(subject);
        });
        
        Button addExamBtn = new Button("📋 시험 추가");
        addExamBtn.setStyle("-fx-background-color: #FFC107; -fx-text-fill: black;");
        addExamBtn.setOnAction(e -> {
            showAddExamDialog(subject);
            // 다이얼로그 새로고침
            dialog.close();
            showTaskManagementDialog(subject);
        });
        
        Button closeBtn = new Button("닫기");
        closeBtn.setOnAction(e -> dialog.close());
        
        buttonBox.getChildren().addAll(addAssignmentBtn, addExamBtn, closeBtn);
        
        content.getChildren().addAll(tabPane, buttonBox);
        
        Scene scene = new Scene(content, 700, 500);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private VBox createAssignmentTabContent(Subject subject) {
        VBox content = new VBox(10);
        
        // 과제 테이블
        TableView<Assignment> assignmentTable = new TableView<>();
        
        TableColumn<Assignment, String> titleCol = new TableColumn<>("제목");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(150);
        
        TableColumn<Assignment, String> subjectCol = new TableColumn<>("과목");
        subjectCol.setCellValueFactory(data -> {
            Assignment assignment = data.getValue();
            Optional<Subject> subjectOpt = dbManager.getSubjectById(assignment.getSubjectId());
            return new javafx.beans.property.SimpleStringProperty(
                subjectOpt.map(Subject::getName).orElse("미지정")
            );
        });
        subjectCol.setPrefWidth(100);
        
        TableColumn<Assignment, String> dueDateCol = new TableColumn<>("마감일");
        dueDateCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getDueDate() != null ? data.getValue().getDueDate().toString() : "미정"
            )
        );
        dueDateCol.setPrefWidth(100);
        
        TableColumn<Assignment, String> priorityCol = new TableColumn<>("우선순위");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(80);
        
        TableColumn<Assignment, String> statusCol = new TableColumn<>("상태");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        
        TableColumn<Assignment, String> daysLeftCol = new TableColumn<>("남은일수");
        daysLeftCol.setCellValueFactory(data -> {
            Assignment assignment = data.getValue();
            long daysLeft = assignment.getDaysLeft();
            String text;
            if (daysLeft < 0) {
                text = "지남(" + Math.abs(daysLeft) + "일)";
            } else if (daysLeft == 0) {
                text = "오늘";
            } else {
                text = daysLeft + "일";
            }
            return new javafx.beans.property.SimpleStringProperty(text);
        });
        daysLeftCol.setPrefWidth(80);
        
        assignmentTable.getColumns().addAll(titleCol, subjectCol, dueDateCol, priorityCol, statusCol, daysLeftCol);
        
        // 데이터 로드
        List<Assignment> assignments;
        if (subject != null) {
            assignments = dbManager.getAssignmentsBySubject(subject.getId());
        } else {
            assignments = dbManager.getAllAssignments();
        }
        assignmentTable.getItems().addAll(assignments);
        
        // 과제 조작 버튼
        HBox assignmentButtonBox = new HBox(10);
        Button editAssignmentBtn = new Button("수정");
        editAssignmentBtn.setOnAction(e -> {
            Assignment selected = assignmentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("선택 오류", "수정할 과제를 선택해주세요!");
                return;
            }
            editAssignment(selected);
        });
        
        Button deleteAssignmentBtn = new Button("삭제");
        deleteAssignmentBtn.setOnAction(e -> {
            Assignment selected = assignmentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("선택 오류", "삭제할 과제를 선택해주세요!");
                return;
            }
            deleteAssignment(selected, assignmentTable);
        });
        
        Button completeBtn = new Button("완료 처리");
        completeBtn.setOnAction(e -> {
            Assignment selected = assignmentTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("선택 오류", "완료 처리할 과제를 선택해주세요!");
                return;
            }
            selected.setStatus("완료");
            dbManager.updateAssignment(selected);
            assignmentTable.refresh();
            showAlert("성공", "과제가 완료 처리되었습니다!");
        });
        
        assignmentButtonBox.getChildren().addAll(editAssignmentBtn, deleteAssignmentBtn, completeBtn);
        
        content.getChildren().addAll(assignmentTable, assignmentButtonBox);
        
        return content;
    }
    
    private VBox createExamTabContent(Subject subject) {
        VBox content = new VBox(10);
        
        // 시험 테이블
        TableView<Exam> examTable = new TableView<>();
        
        TableColumn<Exam, String> titleCol = new TableColumn<>("시험명");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(120);
        
        TableColumn<Exam, String> subjectCol = new TableColumn<>("과목");
        subjectCol.setCellValueFactory(data -> {
            Exam exam = data.getValue();
            Optional<Subject> subjectOpt = dbManager.getSubjectById(exam.getSubjectId());
            return new javafx.beans.property.SimpleStringProperty(
                subjectOpt.map(Subject::getName).orElse("미지정")
            );
        });
        subjectCol.setPrefWidth(100);
        
        TableColumn<Exam, String> typeCol = new TableColumn<>("유형");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(80);
        
        TableColumn<Exam, String> dateTimeCol = new TableColumn<>("시험일시");
        dateTimeCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getExamDateTime() != null ? 
                data.getValue().getExamDateTime().toString().replace("T", " ") : "미정"
            )
        );
        dateTimeCol.setPrefWidth(150);
        
        TableColumn<Exam, String> locationCol = new TableColumn<>("장소");
        locationCol.setCellValueFactory(data -> 
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getLocation() != null ? data.getValue().getLocation() : "미정"
            )
        );
        locationCol.setPrefWidth(100);
        
        TableColumn<Exam, String> statusCol = new TableColumn<>("상태");
        statusCol.setCellValueFactory(data -> {
            Exam exam = data.getValue();
            if (exam.getExamDateTime() == null) {
                return new javafx.beans.property.SimpleStringProperty("미정");
            }
            
            LocalDateTime now = LocalDateTime.now();
            if (exam.getExamDateTime().isBefore(now)) {
                return new javafx.beans.property.SimpleStringProperty("완료");
            } else if (exam.isImminent()) {
                return new javafx.beans.property.SimpleStringProperty("임박");
            } else {
                return new javafx.beans.property.SimpleStringProperty("예정");
            }
        });
        statusCol.setPrefWidth(80);
        
        examTable.getColumns().addAll(titleCol, subjectCol, typeCol, dateTimeCol, locationCol, statusCol);
        
        // 데이터 로드
        List<Exam> exams;
        if (subject != null) {
            exams = dbManager.getExamsBySubject(subject.getId());
        } else {
            exams = dbManager.getAllExams();
        }
        examTable.getItems().addAll(exams);
        
        // 시험 조작 버튼
        HBox examButtonBox = new HBox(10);
        Button editExamBtn = new Button("수정");
        editExamBtn.setOnAction(e -> {
            Exam selected = examTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("선택 오류", "수정할 시험을 선택해주세요!");
                return;
            }
            editExam(selected);
        });
        
        Button deleteExamBtn = new Button("삭제");
        deleteExamBtn.setOnAction(e -> {
            Exam selected = examTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("선택 오류", "삭제할 시험을 선택해주세요!");
                return;
            }
            deleteExam(selected, examTable);
        });
        
        examButtonBox.getChildren().addAll(editExamBtn, deleteExamBtn);
        
        content.getChildren().addAll(examTable, examButtonBox);
        
        return content;
    }
    
    private void editAssignment(Assignment assignment) {
        // 과제 수정 다이얼로그 (추가와 유사하지만 기존 값이 미리 입력됨)
        showAlert("기능 준비중", "과제 수정 기능은 준비중입니다.");
    }
    
    private void deleteAssignment(Assignment assignment, TableView<Assignment> table) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("삭제 확인");
        confirmation.setHeaderText("과제 삭제");
        confirmation.setContentText("정말로 '" + assignment.getTitle() + "' 과제를 삭제하시겠습니까?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbManager.deleteAssignment(assignment.getId());
            table.getItems().remove(assignment);
            showAlert("성공", "과제가 삭제되었습니다!");
        }
    }
    
    private void editExam(Exam exam) {
        // 시험 수정 다이얼로그 (추가와 유사하지만 기존 값이 미리 입력됨)
        showAlert("기능 준비중", "시험 수정 기능은 준비중입니다.");
    }
    
    private void deleteExam(Exam exam, TableView<Exam> table) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("삭제 확인");
        confirmation.setHeaderText("시험 삭제");
        confirmation.setContentText("정말로 '" + exam.getTitle() + "' 시험을 삭제하시겠습니까?");
        
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbManager.deleteExam(exam.getId());
            table.getItems().remove(exam);
            showAlert("성공", "시험이 삭제되었습니다!");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}