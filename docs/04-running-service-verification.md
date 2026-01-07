# RunningService 위치 추적 검증 가이드

`RunningService`를 통해 백그라운드 위치 추적 및 알림 기능을 구현했습니다.

## 변경 사항
- **[수정] [RunningService.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/service/RunningService.kt)**
    - `FusedLocationProviderClient`를 사용한 위치 추적 로직 추가.
    - 포그라운드 서비스 및 알림 채널 구현.
    - 서비스 시작/정지 (`ACTION_START`, `ACTION_STOP`) 처리.
- **[수정] [HomeScreen.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/presentation/home/HomeScreen.kt)**
    - 시작 버튼 클릭 시 `RunningService` 시작 (`ACTION_START`).
    - 정지 버튼 클릭 시 `RunningService` 정지 (`ACTION_STOP`).
    - Android 13+ 대응 알림 권한(`POST_NOTIFICATIONS`) 요청 로직 추가.

## 검증 절차 (수동)

다음 단계를 통해 위치 추적 서비스가 올바르게 작동하는지 확인해주세요.

1. **앱 실행 및 권한 허용**
    - 앱을 실행하면 위치 권한 요청 외에 **알림 권한 요청**이 뜰 수 있습니다 (Android 13 이상). **허용**해주세요.

2. **서비스 시작 테스트**
    - **"START RUN"** 버튼을 클릭합니다.
    - **[확인]** 상단 상태 표시줄(Status bar)에 **"Running Tracker"** 알림이 표시되는지 확인합니다.
    - **[확인]** Logcat에서 `RunningService` 태그로 필터링하여 "Location: ..." 로그가 주기적으로(약 2~5초) 찍히는지 확인합니다.

3. **백그라운드 테스트**
    - 홈 버튼을 눌러 앱을 백그라운드로 보냅니다.
    - **[확인]** 상단 알림이 **계속 유지**되는지 확인합니다.
    - **[확인]** Logcat에서 위치 업데이트 로그가 **계속 찍히는지** 확인합니다.

4. **서비스 종료 테스트**
    - 앱으로 돌아와 **"STOP RUN"** 버튼을 클릭합니다.
    - **[확인]** 상단 알림이 **사라지는지** 확인합니다.
    - **[확인]** Logcat에서 위치 업데이트 로그가 **더 이상 찍히지 않는지** 확인합니다.
