# Running Tracker App - 초기 설정 및 UI 구현 진행 상황

현재까지 진행된 작업 내용을 정리한 문서입니다. 기본 프로젝트 설정부터 핵심 라이브러리 추가, 그리고 메인 UI 구현까지 완료되었습니다.

## 1. 프로젝트 설정 및 보안

### .gitignore 설정
안드로이드 프로젝트에 필수적인 제외 항목들(빌드 결과물, 키스토어 등)을 추가하여 버전 관리를 최적화했습니다.

### Google Maps API Key 보안 설정
API 키를 소스코드에 노출하지 않기 위해 `local.properties`를 활용하는 구조를 구축했습니다.
- `local.properties`: `GOOGLE_MAPS_API_KEY` 변수 추가
- `app/build.gradle.kts`: `manifestPlaceholders`를 통해 키 주입
- `AndroidManifest.xml`: `<meta-data>` 태그로 키 사용

## 2. 라이브러리 및 의존성 추가
`gradle/libs.versions.toml` 및 `app/build.gradle.kts`에 다음 핵심 라이브러리를 추가했습니다.
- **Room**: 로컬 데이터베이스 저장 (v2.6.1)
- **Maps Compose**: Jetpack Compose용 Google Maps (v4.4.1)
- **Play Services Location**: 위치 추적 서비스 (v21.0.1)
- **Material Icons Extended**: 추가적인 Material Icon 사용 (`Icons.Default.Stop` 등)
- **KSP**: Room 컴파일러용 플러그인 (v2.0.21-1.0.27)

## 3. 앱 권한 및 서비스 구성
`AndroidManifest.xml`에 위치 추적 및 서비스 실행을 위한 권한을 선언했습니다.
- `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`: 위치 정보 접근
- `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_LOCATION`: 백그라운드 위치 추적
- `POST_NOTIFICATIONS`: 알림 표시
- `WAKE_LOCK`: 화면 꺼짐 방지

또한, 백그라운드 위치 추적을 담당할 `RunningService` (Skeleton)를 생성하고 등록했습니다.

## 4. UI 구현 (Google Maps & Controls)
`HomeScreen.kt`를 생성하여 메인 화면을 구현했습니다.
- **GoogleMap**: 전체 화면에 지도 표시 (서울 시청 중심 초기화)
- **상단 정보창**: 운동 시간, 거리(km), 페이스(min/km)를 표시하는 반투명 오버레이
- **하단 컨트롤**: 시작/정지 토글 버튼 (스타일 및 상태 변화 적용)

### 스크린샷 예시 (구현된 UI 구조)
- **시작 전**: 녹색 "START RUN" 버튼
- **운동 중**: 빨간색 "STOP RUN" 버튼, 지도 위에 실시간 경로 및 정보 표시 (기능 연동 예정)

## 다음 단계
- `RunningService`에 실제 위치 추적 로직(LocationCallback) 구현 (Task 4)
- Room Database (`RunRecord`) 설계 및 저장 로직 구현 (Task 5)
- UI와 서비스 간 데이터 연동 (Task 3, 4)
