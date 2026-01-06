# 러닝 트래커 앱 구현 계획

사용자 요구사항에 맞춰 위치 추적, 지도 표시, 데이터 저장이 가능한 러닝 트래커 앱을 구현합니다.

## 변경 제안

### 1. 의존성 추가 (Dependencies)
#### [수정] [gradle/libs.versions.toml](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/gradle/libs.versions.toml)
- Room Database (`androidx.room`)
- Google Maps Compose (`com.google.maps.android:maps-compose`)
- Play Services Location (`com.google.android.gms:play-services-location`)
- Accompanist Permissions (선택사항, 또는 ActivityResultContracts 사용)

#### [수정] [app/build.gradle.kts](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/app/build.gradle.kts)
- 위 라이브러리 implement. `kapt` 또는 `ksp` 플러그인 추가 (Room 용).

### 2. 핵심 컴포넌트 설계

#### A. 데이터베이스 (Room)
- **Entity**: `RunRecord` (id, startTime, endTime, distance, duration, pathPointsJson)
- **DAO**: `RunDao` (insert, getAll)
- **Database**: `AppDatabase`

#### B. 포그라운드 서비스 (LocationService)
- `RunningService`: 백그라운드에서도 위치를 수집.
- **Notification**: "운동 중" 상태 표시.
- **WakeLock**: 운동 중 화면 꺼짐 방지 및 CPU 깸 상태 유지.
- **LocationCallback**: 위치 업데이트 수신 -> UI로 전달 (Repository 또는 Flow 사용).

#### C. UI (Jetpack Compose)
- **HomeScreen**:
    - **GoogleMap**: 경로(Polyline) 표시. 내 위치 표시.
    - **Overlay**: 시간, 거리, 속도 정보 표시.
    - **Controls**: 시작/일시정지/정지 버튼.
- **PermissionRequest**: 위치 권한(FINE_LOCATION, COARSE_LOCATION), 알림 권한(POST_NOTIFICATIONS) 요청.

### 3. 기능 흐름
1. **시작**: 권한 확인 -> 서비스 시작 (`startService`) -> Notification 표시 -> Wakelock 획득.
2. **운동 중**: 위치 업데이트 -> `Route` 리스트에 좌표 추가 -> UI에서 Polyline 그리기 -> 거리 계산.
3. **정지**: 서비스 중단 (`stopSelf`) -> DB에 기록 저장 -> Wakelock 해제 -> 결과 화면(또는 토스트) 표시.

## 검증 계획
### 자동화 테스트
- DAO 단위 테스트.
- 서비스 생명주기 테스트 (가능한 범위 내).

### 수동 검증
- **시작 버튼**: 알림창 생성 확인.
- **이동 시**: 지도에 선이 그려지는지 확인.
- **백그라운드 전환**: 앱을 내려도 알림이 유지되고 위치 수집 로그가 남는지 확인.
- **종료**: DB에 저장된 기록이 불려오는지 확인 (Log 또는 별도 화면).
