# RunningService 위치 추적 구현 계획

## 목표 설명
앱이 백그라운드에 있을 때도 사용자의 러닝 경로를 추적하기 위해 `RunningService`를 구현합니다. `FusedLocationProviderClient`를 사용하여 위치 업데이트를 수신하고, 포그라운드 서비스 알림을 통해 앱이 실행 중임을 사용자에게 알립니다.

## 사용자 검토 필요 사항
> [!NOTE]
> 안드로이드 14 이상에서는 포그라운드 서비스 타입(`location`)이 필수이며 이미 매니페스트에 선언되어 있습니다. 알림 권한(`POST_NOTIFICATIONS`)은 런타임에 요청해야 합니다.

## 제안된 변경 사항

### Service 레이어

#### [수정] [RunningService.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/service/RunningService.kt)
- `FusedLocationProviderClient` 초기화.
- `NotificationChannel` 생성 및 포그라운드 서비스 시작 (`startForeground`).
- `LocationCallback`을 구현하여 위치 업데이트 수신.
- 서비스 시작(`ACTION_START`), 중지(`ACTION_STOP`) 인텐트 명령 처리.
- 위치 데이터를 브로드캐스트하거나 SharedFlow/StateFlow로 UI에 전달 (초기 단계에서는 로그로 확인).

### UI 레이어

#### [수정] [HomeScreen.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/presentation/home/HomeScreen.kt)
- 시작/정지 버튼 클릭 시 `RunningService`에 인텐트 전송 (`startService`, `stopService`).
- 알림 권한 요청 로직 추가 (필요 시).

## 검증 계획

### 수동 검증
1. **서비스 시작**: 앱에서 "START RUN" 버튼 클릭.
    - 상단 상태바에 "Running Tracker" 알림이 표시되는지 확인.
    - Logcat에서 위치 업데이트 로그가 찍히는지 확인.
2. **백그라운드 테스트**: 앱을 홈 화면으로 내린 후에도 알림이 유지되고 로그가 계속 찍히는지 확인.
3. **서비스 종료**: "STOP RUN" 버튼 클릭.
    - 알림이 사라지는지 확인.
    - 위치 업데이트 로그가 중지되는지 확인.
