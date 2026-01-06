# Room Database 구현 계획

## 목표 설명
러닝 기록(시간, 거리, 평균 속도, 경로 이미지, 날짜 등)을 로컬 기기에 영구 저장하기 위해 Room Database를 구축합니다.

## 제안된 변경 사항

### Data 레이어 (New)

#### [NEW] [RunRecord.kt](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/app/src/main/java/com/ezlevup/runningtrackerv2/data/RunRecord.kt)
- `@Entity(tableName = "run_table")`
- 필드:
    - `id`: Primary Key (Auto Generate)
    - `img`: Bitmap (경로 스냅샷) -> TypeConverter 필요
    - `timestamp`: Long (러닝 시간/날짜)
    - `avgSpeedInKMH`: Float
    - `distanceInMeters`: Int
    - `timeInMillis`: Long (운동 소요 시간)
    - `caloriesBurned`: Int

#### [NEW] [RunDao.kt](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/app/src/main/java/com/ezlevup/runningtrackerv2/data/RunDao.kt)
- `insertRun(run: RunRecord)`
- `deleteRun(run: RunRecord)`
- `getAllRunsSortedByDate()`: Flow<List<RunRecord>>
- 그 외 정렬 기준별 쿼리 (거리순, 시간순 등)

#### [NEW] [Converters.kt](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/app/src/main/java/com/ezlevup/runningtrackerv2/data/Converters.kt)
- `Bitmap` <-> `ByteArray` 변환 메서드 구현

#### [NEW] [RunningDatabase.kt](file:///c:/workdir/space-cap/AndroidStudioProjects/RunningTrackerV2/app/src/main/java/com/ezlevup/runningtrackerv2/data/RunningDatabase.kt)
- `@Database` 어노테이션 정의
- 싱글톤 패턴으로 DB 인스턴스 제공

## 검증 계획

### 자동화 테스트 (선택 사항)
- `androidTest` 폴더에 `RunDaoTest`를 생성하여 인메모리 DB에서 Insert 및 Read 테스트 수행.
