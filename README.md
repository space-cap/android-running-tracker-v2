# 🏃‍♂️ Running Tracker V2

러닝 경로를 실시간으로 추적하고 기록하는 안드로이드 애플리케이션입니다.
백그라운드 위치 추적, 구글 지도 연동, 그리고 운동 기록 저장을 지원합니다.

## ✨ 주요 기능

*   **실시간 위치 추적**: Google Maps SDK와 Fused Location Provider를 사용하여 러닝 경로를 지도에 표시합니다.
*   **백그라운드 지원**: `RunningService` (Foreground Service)를 통해 앱이 백그라운드에 있어도 끈김 없이 위치를 기록합니다.
*   **상태 알림**: 러닝 중 실시간 상태를 알림 바(Notification)를 통해 확인할 수 있습니다.
*   **데이터 저장**: Room Database를 사용하여 시간, 거리, 평균 속도, 경로 이미지 등의 러닝 기록을 로컬에 저장합니다.
*   **권한 관리**: 위치 권한 (`ACCESS_FINE_LOCATION`) 및 알림 권한 (`POST_NOTIFICATIONS`)을 안전하게 처리합니다.

## 🛠 기술 스택

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)
*   **Architecture**: MVVM Pattern (진행 중)
*   **Asynchronous**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
*   **Dependency Injection**: (추후 적용 예정 - Hilt/Koin)
*   **Local Storage**: [Room Database](https://developer.android.com/training/data-storage/room)
*   **Maps & Location**:
    *   [Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk/overview) (Maps Compose)
    *   [Fused Location Provider Client](https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient)

## 📂 프로젝트 구조

```
com.ezlevup.runningtrackerv2
├── data            # Room DB, Entity, DAO
├── ui              # Compose UI Screens
├── RunningService  # 백그라운드 위치 추적 서비스
└── MainActivity    # 진입점
```

## 🚀 시작하기 (Getting Started)

### 사전 요구사항
*   Android Studio Ladybug 이상
*   Android SDK 34+
*   **Google Maps API Key**

### 설정 방법

1.  **Repository 복제**
    ```bash
    git clone https://github.com/your-username/android-running-tracker-v2.git
    ```

2.  **API Key 설정**
    *   `local.properties` 파일을 프로젝트 루트에 생성합니다 (이미 있다면 수정).
    *   발급받은 Google Maps API Key를 추가합니다.
    ```properties
    GOOGLE_MAPS_API_KEY=AIzaSy...
    ```

3.  **빌드 및 실행**
    *   Android Studio에서 프로젝트를 엽니다.
    *   `Run` 버튼을 눌러 에뮬레이터 또는 실기기에서 실행합니다.

## 📚 문서 (Documentation)

자세한 개발 기록과 계획은 `docs/` 폴더에서 확인하실 수 있습니다.

*   [📅 진행 상황 리포트](docs/02-progress-report.md)
*   [🛠 Service 구현 계획](docs/03-running-service-plan.md)
*   [✅ Service 검증 가이드](docs/04-running-service-verification.md)
*   [💾 Database 구현 계획](docs/05-room-database-plan.md)
