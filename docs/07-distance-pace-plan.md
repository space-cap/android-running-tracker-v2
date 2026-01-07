# 거리 및 페이스 구현 계획

## 문제점
현재 UI의 거리(km)와 페이스(min/km)가 "0.00"과 "0:00"으로 고정되어 있어 실제 러닝 정보를 반영하지 못하고 있습니다.

## 해결 방안
`TrackingManager`에 거리 계산 로직을 추가하고, 이를 UI에 연동하여 실시간으로 거리와 페이스를 표시합니다.

### 1. [수정] [`util/TrackingManager.kt`](../app/src/main/java/com/ezlevup/runningtrackerv2/util/TrackingManager.kt)
- **상태 추가**:
    - `distanceInMeters`: 누적 거리를 저장하는 상태 (Int).
    - `pathPoints`: 이동 경로 좌표들을 저장하는 리스트 (`List<LatLng>`).
- **로직 추가**:
    - `addPathPoint(location: Location)`:
        - 새로운 위치를 리스트에 추가합니다.
        - 이전 위치가 있다면 `distanceTo()`를 사용하여 거리를 계산하고 `distanceInMeters`에 누적합니다.

### 2. [수정] [`RunningService.kt`](../app/src/main/java/com/ezlevup/runningtrackerv2/RunningService.kt)
- `onLocationResult` 콜백 내에서:
    - 위치 업데이트가 발생할 때마다 `TrackingManager.addPathPoint(location)`를 호출하여 데이터를 전달합니다.

### 3. [수정] [`ui/HomeScreen.kt`](../app/src/main/java/com/ezlevup/runningtrackerv2/ui/HomeScreen.kt)
- **거리 표시**: `TrackingManager.distanceInMeters`를 구독하여 `km` 단위(소수점 2자리)로 변환해 표시합니다.
- **페이스 계산**:
    - 공식: `(경과 시간(분)) / (이동 거리(km))`
    - `TrackingManager.durationInMillis`와 `distanceInMeters`를 사용하여 실시간으로 계산 및 표시합니다.
- **(선택 사항)**: 지도에 이동 경로(Polyline) 그리기 (이후 단계에서 진행).

## 검증 계획
1. **에뮬레이터 테스트**
    - 앱 실행 후 "START RUN" 클릭.
    - 에뮬레이터 설정(Extended Controls) > Location에서 위도/경도를 변경하며 이동 시뮬레이션.
    - UI의 거리가 증가하는지 확인.
    - 시간이 흐름에 따라 페이스가 계산되어 나오는지 확인 (예: 1km를 5분에 이동 시 5:00 min/km).

2. **단위 변환 확인**
    - 1000m -> 1.00km
    - 500m -> 0.50km
