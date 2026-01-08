# 배터리 잔량 확인 기능 구현 계획

## 목표 설명
운동 시작 전 기기의 배터리 잔량을 확인하는 기능을 구현합니다. Github Issue #1에 따라 배터리가 **30% 이하**일 경우, 사용자에게 경고 다이얼로그를 표시하여 진행 여부를 확인합니다.

> **참고**: 운동 중 배터리 부족 시 강제 종료 기능은 별도의 이슈로 분리하여 추후 진행합니다.

## 변경 제안

### 유틸리티
#### [NEW] [BatteryUtils.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/util/BatteryUtils.kt)
- **목적**: 배터리 관련 로직을 분리하여 재사용성 및 가독성 향상.
- **기능**:
    - `getBatteryPercentage(context: Context): Int`: 현재 배터리 잔량(%) 반환.
    - `isCharging(context: Context): Boolean`: 현재 충전 중인지 여부 반환.

### 프레젠테이션
#### [MODIFY] [HomeScreen.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/presentation/home/HomeScreen.kt)
- **상태 추가**:
    - `showLowBatteryDialog` (Boolean): 배터리 부족 경고창 표시 여부 관리.
- **로직 수정 (START RUN 버튼)**:
    - 클릭 시 `BatteryUtils.getBatteryPercentage` 호출.
    - **조건**: 배터리 <= 30% **AND** 충전 중이 아님.
    - **True**: `showLowBatteryDialog = true` 설정 (경고창 띄움).
    - **False**: 기존 로직대로 즉시 `startService` 호출.
- **UI 추가**:
    - `AlertDialog`: 경고 메시지 표시.
        - "취소": 다이얼로그 닫기.
        - "계속하기": 다이얼로그 닫고 `startService` 호출.

## 검증 계획

### 수동 검증
1.  **배터리 부족 시나리오 (30% 이하)**:
    -   에뮬레이터 배터리 잔량을 **25%**로 설정 (충전 X).
    -   "START RUN" 버튼 클릭.
    -   경고 다이얼로그("배터리가 부족합니다...") 출력 확인.
    -   "취소" -> 운동 시작 안 됨 확인.
    -   "계속하기" -> 운동 시작 됨 확인.
2.  **배터리 충분 시나리오**:
    -   에뮬레이터 배터리 잔량을 **35%**로 설정.
    -   "START RUN" 버튼 클릭.
    -   경고 없이 즉시 운동 시작 확인.
