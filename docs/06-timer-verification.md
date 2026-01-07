# 타이머 기능 검증 가이드

UI의 시간이 작동하지 않는 문제를 해결하기 위해 **TrackingManager**를 도입하고 서비스와 UI를 연동했습니다.

## 변경 사항
- **[NEW] [TrackingManager.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/util/TrackingManager.kt)**: 러닝 시간(Stopwatch)과 상태(`isTracking`)를 관리하는 싱글톤 객체.
- **[NEW] [FormatUtils.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/util/FormatUtils.kt)**: 밀리초 단위를 `HH:MM:SS` 형식으로 변환하는 유틸리티.
- **[수정] [RunningService.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/RunningService.kt)**: 서비스 시작/종료 시 `TrackingManager`의 타이머를 `startResumeTimer()` / `stopTimer()`로 제어하도록 수정.
- **[수정] [HomeScreen.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/ui/HomeScreen.kt)**:
    - 로컬 상태(`isTracking`) 제거 -> `TrackingManager.isTracking` 관찰.
    - 하드코딩된 시간 제거 -> `TrackingManager.durationInMillis`를 관찰하여 실시간 표시.

## 검증 절차 (수동)

1. **앱 실행 및 시작**
    - "START RUN" 버튼을 클릭합니다.
    - **[확인]** 중앙의 시간 텍스트가 `00:00:01`, `00:00:02`... 와 같이 증가하는지 확인합니다.

2. **서비스 연동 확인**
    - 상단 알림 바에 "Running Tracker"가 표시되는지 확인합니다. (Service가 실행 중이어야 타이머도 동작)

3. **종료 테스트**
    - "STOP RUN" 버튼을 클릭합니다.
    - **[확인]** 시간이 `00:00:00`으로 초기화되고 버튼이 "START RUN"으로 변경되는지 확인합니다.
