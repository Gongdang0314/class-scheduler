package timetable;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import common.model.Subject;
import common.database.DatabaseManager;

/**
 * 완전한 기능을 갖춘 시간표 패널
 * TimetableController와 AssignmentExamManager를 통합하여 제공
 */
public class EnhancedTimetablePanel extends VBox {
    
    private TimetableController timetableController;
    private AssignmentExamManager taskManager;
    private DatabaseManager dbManager;
    
    public EnhancedTimetablePanel() {
        this.dbManager = DatabaseManager.getInstance();
        this.timetableController = new TimetableController();
        this.taskManager = new AssignmentExamManager();
        
        setupLayout();
        setupStyles();
    }
    
    private void setupLayout() {
        // 상단 메뉴 바
        MenuBar menuBar = createMenuBar();
        
        // 상단 빠른 액션 버튼들
        HBox quickActionBar = createQuickActionBar();
        
        // 메인 시간표 컨트롤러
        VBox.setVgrow(timetableController, Priority.ALWAYS);
        
        this.getChildren().addAll(menuBar, quickActionBar, timetableController);
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // 파일 메뉴
        Menu fileMenu = new Menu("파일");
        MenuItem saveItem = new MenuItem("저장");
        saveItem.setOnAction(e -> dbManager.saveAllData());
        
        MenuItem loadItem = new MenuItem("불러오기");
        loadItem.setOnAction(e -> {
            dbManager.reloadData();
            refreshTimetable();
        });
        
        MenuItem backupItem = new MenuItem("백업 생성");
        backupItem.setOnAction(e -> dbManager.createBackup());
        
        fileMenu.getItems().addAll(saveItem, loadItem, backupItem);
        
        // 과제/시험 메뉴
        Menu taskMenu = new Menu("과제/시험");
        MenuItem addAssignmentItem = new MenuItem("📝 과제 추가");
        addAssignmentItem.setOnAction(e -> taskManager.showAddAssignmentDialog(null));
        
        MenuItem addExamItem = new MenuItem("📋 시험 추가");
        addExamItem.setOnAction(e -> taskManager.showAddExamDialog(null));
        
        MenuItem manageTasksItem = new MenuItem("📊 과제/시험 관리");
        manageTasksItem.setOnAction(e -> taskManager.showTaskManagementDialog(null));
        
        taskMenu.getItems().addAll(addAssignmentItem, addExamItem, manageTasksItem);
        
        // 도구 메뉴
        Menu toolsMenu = new Menu("도구");
        MenuItem statsItem = new MenuItem("📈 통계 보기");
        statsItem.setOnAction(e -> showStatistics());
        
        MenuItem clearItem = new MenuItem("🧹 데이터 초기화");
        clearItem.setOnAction(e -> clearAllData());
        
        toolsMenu.getItems().addAll(statsItem, clearItem);
        
        menuBar.getMenus().addAll(fileMenu, taskMenu, toolsMenu);
        return menuBar;
    }
    
    private HBox createQuickActionBar() {
        HBox actionBar = new HBox(10);
        actionBar.setPadding(new Insets(10));
        actionBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6;");
        
        Button quickAddAssignment = new Button("📝 빠른 과제 추가");
        quickAddAssignment.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white;");
        quickAddAssignment.setOnAction(e -> taskManager.showAddAssignmentDialog(null));
        
        Button quickAddExam = new Button("📋 빠른 시험 추가");
        quickAddExam.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black;");
        quickAddExam.setOnAction(e -> taskManager.showAddExamDialog(null));
        
        Button viewAllTasks = new Button("📊 전체 과제/시험");
        viewAllTasks.setStyle("-fx-background-color: #6f42c1; -fx-text-fill: white;");
        viewAllTasks.setOnAction(e -> taskManager.showTaskManagementDialog(null));
        
        Button refreshBtn = new Button("🔄 새로고침");
        refreshBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> refreshTimetable());
        
        // 상태 표시 라벨
        Label statusLabel = new Label("💾 자동 저장됨");
        statusLabel.setStyle("-fx-text-fill: #6c757d;");
        
        // 여백을 위한 공간
        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        actionBar.getChildren().addAll(
            quickAddAssignment, 
            quickAddExam, 
            viewAllTasks, 
            refreshBtn,
            spacer,
            statusLabel
        );
        
        return actionBar;
    }
    
    private void refreshTimetable() {
        // TimetableController의 새로고침 메서드 호출
        // (실제로는 TimetableController에 public refresh 메서드를 추가해야 함)
        dbManager.reloadData();
        showAlert("새로고침", "시간표가 새로고침되었습니다!");
    }
    
    private void showStatistics() {
        String stats = dbManager.getDatabaseStatus();
        showAlert("📈 통계", stats);
    }
    
    private void clearAllData() {
        javafx.scene.control.Alert confirmation = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("데이터 초기화");
        confirmation.setHeaderText("모든 데이터 삭제");
        confirmation.setContentText("정말로 모든 데이터를 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다!");
        
        java.util.Optional<javafx.scene.control.ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            dbManager.clearAllData();
            refreshTimetable();
            showAlert("완료", "모든 데이터가 삭제되었습니다.");
        }
    }
    
    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void setupStyles() {
        this.setStyle("-fx-background-color: #ffffff;");
        this.setSpacing(0);
    }
    
    /**
     * 외부에서 특정 과목의 과제/시험을 관리할 때 사용
     */
    public void showSubjectTasks(Subject subject) {
        taskManager.showTaskManagementDialog(subject);
    }
    
    /**
     * 과제 추가 다이얼로그 직접 호출
     */
    public void addAssignmentForSubject(Subject subject) {
        taskManager.showAddAssignmentDialog(subject);
    }
    
    /**
     * 시험 추가 다이얼로그 직접 호출
     */
    public void addExamForSubject(Subject subject) {
        taskManager.showAddExamDialog(subject);
    }
}