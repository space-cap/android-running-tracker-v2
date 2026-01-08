# 불필요한 백그라운드 위치 권한 확인 제거

## 목표 설명
현재 앱이 지속적으로 "항상 허용(Background Location)" 권한을 요구하는 팝업을 띄우고 있습니다. 하지만 앱은 `foregroundServiceType="location"`을 사용하는 **Foreground Service** 방식으로 작동하므로, "앱 사용 중에만 허용" 권한만으로도 화면이 꺼지거나 다른 앱 사용 중에도 정상적인 위치 추적이 가능합니다.
사용자 경험을 저해하는 이 불필요한 팝업을 제거합니다.

## 변경 제안

### 프레젠테이션 계층
#### [MODIFY] [HomeScreen.kt](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/app/src/main/java/com/ezlevup/runningtrackerv2/presentation/home/HomeScreen.kt)
- **삭제**: `showBackgroundLocationDialog` 상태 변수 및 관련 로직.
- **삭제**: `hasBackgroundLocationPermission` 확인 로직.
- **삭제**: 다이얼로그를 트리거하는 `LaunchedEffect`.

## 검증 계획
1.  **빌드**: 클린 빌드 수행.
2.  **수동 테스트**:
    - 앱 삭제 (권한 초기화).
    - 설치 및 실행.
    - "앱 사용 중에만 허용" 권한 부여.
    - "백그라운드 위치 권한 필요" 팝업이 **뜨지 않는지** 확인.
    - 러닝 시작.
    - 홈 화면으로 나가기 (백그라운드 진입).
    - 알림바에 "Tracking your run..."이 표시되고 시간이 계속 흐르는지 확인.
