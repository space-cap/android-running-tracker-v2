package com.ezlevup.runningtrackerv2.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertRun(run: RunRecord)

    @Delete suspend fun deleteRun(run: RunRecord)

    @Query("SELECT * FROM run_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): Flow<List<RunRecord>>

    @Query("SELECT * FROM run_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): Flow<List<RunRecord>>

    @Query("SELECT * FROM run_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): Flow<List<RunRecord>>

    @Query("SELECT * FROM run_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): Flow<List<RunRecord>>

    @Query("SELECT * FROM run_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): Flow<List<RunRecord>>

    @Query("SELECT SUM(timeInMillis) FROM run_table") fun getTotalTimeInMillis(): Flow<Long>

    @Query("SELECT SUM(caloriesBurned) FROM run_table") fun getTotalCaloriesBurned(): Flow<Int>

    @Query("SELECT SUM(distanceInMeters) FROM run_table") fun getTotalDistance(): Flow<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM run_table") fun getTotalAvgSpeed(): Flow<Float>
}
