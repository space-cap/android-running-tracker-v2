# 구현 결과 - 배터리 부족 시 강제 저장

"배터리 부족 시 강제 저장" 기능(Issue #2)을 구현했습니다.

## 변경 사항

### 1. RunningService.kt
- **배터리 모니터링**: `ACTION_BATTERY_CHANGED`를 수신하는 `BroadcastReceiver`를 추가했습니다.
- **강제 저장 로직**: 러닝 기록 중 배터리가 20% 미만으로 떨어지면:
    - 현재 러닝 데이터를 자동으로 DB에 저장합니다.
    - 서비스와 위치 업데이트를 중지합니다.
    - 사용자에게 "배터리 부족으로 인해 저장되었습니다"라는 알림을 보냅니다.

### 2. TrackingManager.kt
- `createRunRecord(bitmap: Bitmap?)`: `RunRecord` 객체 생성 로직을 중앙화하기 위한 헬퍼 함수를 추가했습니다.

### 3. HomeViewModel.kt
- `saveRun` 함수가 공유된 `TrackingManager.createRunRecord` 메서드를 사용하도록 리팩토링했습니다.

## 검증 결과

### 빌드 검증
- `assembleDebug`: **성공**

### 수동 검증 필요
에뮬레이터에서 다음 단계를 수행하여 동작을 확인해야 합니다:
1.  러닝을 시작합니다.
2.  터미널에서 `adb shell cmd battery unplug` 실행.
3.  터미널에서 `adb shell cmd battery set level 15` 실행.
4.  러닝이 멈추고 "러닝 기록(Run List)"에 저장되었는지 확인합니다.
