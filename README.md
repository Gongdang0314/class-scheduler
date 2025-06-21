# class-scheduler
1. feature/ui-integration
담당자: 임민수

목적: 전체 UI 구성, CSS 통일, 스타일 개선

세부 작업
각 패널의 UI 통일 (VBox, TableView, GridPane 등)

styles.css 관리

버튼/테이블/차트/입력창 등 UI 요소 디자인

2. feature/data-management
담당자: 김민재

목적: 데이터베이스 / 파일 저장/불러오기 / 공통 모델 및 유틸

세부 작업
common/model/*.java → DTO 정의

common/database/FileManager.java, DatabaseManager.java

GradeUtils.java, DateUtils.java 작성

data/ 폴더 구조 구성

3. feature/timetable-integration
담당자: 김대영

목적: 수업 시간표 + 과제/시험 연동 기능 로직

세부 작업
ClassTimetablePanel.java UI

AssignmentExamPanel과 연동 로직

onSubjectClick() 등 이벤트 핸들링

시간 충돌 검사, 수업 클릭 시 과제/시험 출력

4. feature/grade-study-logic
담당자: 황태웅

목적: 학점 계산기 + 공부 시간 기록/통계 기능 구현

세부 작업
GradeCalculatorPanel.java UI + 계산 로직

GPA 계산, 이수학점, 졸업요건

StudySchedulePanel.java + 통계/달성률/목표 처리

