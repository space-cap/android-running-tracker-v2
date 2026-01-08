# 구현 결과 - 백그라운드 위치 권한 확인 제거

불필요하게 지속적으로 뜨던 "백그라운드 위치 권한 필요" 팝업 문제를 해결했습니다.

## 변경 사항

### HomeScreen.kt
- `showBackgroundLocationDialog` 변수 및 로직 삭제.
- `hasBackgroundLocationPermission` 확인 로직 삭제.
- `LaunchedEffect` 삭제.

## 결과
앱이 더 이상 "항상 허용"을 요구하는 다이얼로그를 띄우지 않습니다. 포그라운드 서비스를 사용하므로 "앱 사용 중에만 허용" 권한만으로도 정상적으로 위치 추적 및 기록이 가능합니다.

## 검증 결과
- `assembleDebug`: **성공**
- 수동 검증: 에뮬레이터에서 앱 재설치 후 권한 요청 흐름 확인 필요.
