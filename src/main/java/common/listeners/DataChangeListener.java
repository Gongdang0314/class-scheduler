package common.listeners;

/**
 * 데이터 변경 시 알림을 받을 수 있는 리스너 인터페이스
 * Observer 패턴을 구현하여 데이터 동기화를 지원합니다.
 */
public interface DataChangeListener {
    
    /**
     * 과목 데이터가 변경되었을 때 호출됩니다.
     * @param changeType 변경 유형 ("ADD", "UPDATE", "DELETE")
     * @param subjectId 변경된 과목의 ID (삭제의 경우 -1일 수 있음)
     */
    void onSubjectChanged(String changeType, int subjectId);
    
    /**
     * 과제 데이터가 변경되었을 때 호출됩니다.
     * @param changeType 변경 유형 ("ADD", "UPDATE", "DELETE")
     * @param assignmentId 변경된 과제의 ID
     */
    default void onAssignmentChanged(String changeType, int assignmentId) {
        // 기본적으로 아무것도 하지 않음 (선택적 구현)
    }
    
    /**
     * 시험 데이터가 변경되었을 때 호출됩니다.
     * @param changeType 변경 유형 ("ADD", "UPDATE", "DELETE")
     * @param examId 변경된 시험의 ID
     */
    default void onExamChanged(String changeType, int examId) {
        // 기본적으로 아무것도 하지 않음 (선택적 구현)
    }
    
    /**
     * 성적 데이터가 변경되었을 때 호출됩니다.
     * @param changeType 변경 유형 ("ADD", "UPDATE", "DELETE")
     * @param gradeId 변경된 성적의 ID
     */
    default void onGradeChanged(String changeType, int gradeId) {
        // 기본적으로 아무것도 하지 않음 (선택적 구현)
    }
}