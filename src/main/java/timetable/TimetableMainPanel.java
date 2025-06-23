package timetable;

import javafx.scene.layout.VBox;

/**
 * 메인 애플리케이션에서 사용할 시간표 패널
 * 기존 ui.panels.TimetablePanel 대신 사용할 수 있습니다.
 */
public class TimetableMainPanel extends VBox {
    
    private EnhancedTimetablePanel enhancedPanel;
    
    public TimetableMainPanel() {
        this.enhancedPanel = new EnhancedTimetablePanel();
        this.getChildren().add(enhancedPanel);
        
        // 전체 화면을 채우도록 설정
        VBox.setVgrow(enhancedPanel, javafx.scene.layout.Priority.ALWAYS);
    }
    
    /**
     * 특정 과목의 과제/시험을 관리하고 싶을 때 사용
     */
    public void manageSubjectTasks(common.model.Subject subject) {
        enhancedPanel.showSubjectTasks(subject);
    }
}