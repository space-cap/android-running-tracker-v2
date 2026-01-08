# 배터리 부족 시 강제 저장 (Issue #2)

## 목표 설명
기기의 배터리가 방전되어 데이터가 유실되는 것을 방지하기 위해, 배터리 잔량이 **20% 미만**으로 떨어지면 앱이 자동으로 현재 러닝을 저장하고 서비스를 종료해야 합니다.

## 사용자 검토 필요
> [!IMPORTANT]
> "강제 저장" 시에는 앱이 백그라운드에 있을 수 있으므로 지도 스냅샷(Bitmap)을 캡처할 수 **없습니다**. 따라서 러닝 기록은 기본 이미지 또는 빈 이미지로 저장됩니다.

## 변경 제안

### 서비스 계층 (Service Layer)
#### [MODIFY] [RunningService.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/service/RunningService.kt)
- **배터리 모니터링**:
    - `onCreate`에서 `Intent.ACTION_BATTERY_CHANGED`에 대한 `BroadcastReceiver`를 등록합니다.
    - 배터리 잔량을 지속적으로 모니터링합니다.
    - 조건: 배터리 < 20% **그리고** `TrackingManager.isTracking`이 `true`인 경우:
        - `forceSaveRun()`을 트리거합니다.
- **강제 저장 로직**:
    - `(application as BaseApplication).database.runDao()`를 통해 `RunDao`를 획득합니다.
    - `TrackingManager`의 데이터를 사용하여 `RunRecord`를 생성합니다 (Bitmap은 null 처리).
    - 코루틴(`Dispatchers.IO`)을 사용하여 DB에 삽입합니다.
    - 서비스를 종료하고 "배터리 부족으로 인해 저장되었습니다"라는 알림을 표시합니다.

### 유틸리티 / 로직 (Utility / Logic)
#### [MODIFY] [TrackingManager.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/util/TrackingManager.kt)
- **리팩토링**:
    - `createRunRecord(bitmap: Bitmap?): RunRecord` 헬퍼 함수를 추가하여 `RunRecord` 객체 생성 로직(칼로리 계산, 평균 속도 등)을 중앙화합니다. 이를 통해 `HomeViewModel`과 `RunningService` 간의 코드 중복을 방지합니다.

#### [MODIFY] [HomeViewModel.kt](../app/src/main/java/com/ezlevup/runningtrackerv2/presentation/home/HomeViewModel.kt)
- **리팩토링**:
    - `saveRun` 함수가 `TrackingManager.createRunRecord`를 사용하도록 업데이트합니다.

## 검증 계획

### 수동 검증
1.  **러닝 시작**: 변경 사항을 적용하고 에뮬레이터에서 앱을 실행하여 러닝을 시작합니다.
2.  **배터리 부족 시뮬레이션**:
    - 터미널을 엽니다.
    - 실행: `adb shell cmd battery unplug`
    - 실행: `adb shell cmd battery set level 15`
3.  **동작 확인**:
    - **서비스**: 서비스가 종료되어야 합니다 (알림이 변경되거나 사라짐).
    - **알림**: "배터리 부족으로 러닝이 저장되었습니다" (또는 유사한 메시지)가 표시되는지 확인합니다.
    - **데이터**: "러닝 기록(Run List)" 화면을 엽니다. 해당 기록이 저장되어 있는지 확인합니다 (지도 이미지는 없을 수 있음).
4.  **배터리 재설정**:
    - 실행: `adb shell cmd battery reset`
