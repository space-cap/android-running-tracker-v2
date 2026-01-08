package com.ezlevup.runningtrackerv2.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezlevup.runningtrackerv2.data.RunDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class StatisticsSummary(
        val totalDistance: Int = 0,
        val totalTime: Long = 0L,
        val totalCalories: Int = 0,
        val avgSpeed: Float = 0f
)

data class StatisticsState(
        val total: StatisticsSummary = StatisticsSummary(),
        val weekly: StatisticsSummary = StatisticsSummary(),
        val monthly: StatisticsSummary = StatisticsSummary()
)

class StatisticsViewModel(private val runDao: RunDao) : ViewModel() {

        private val calendar = java.util.Calendar.getInstance()

        private fun getStartOfWeek(): Long {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                return calendar.timeInMillis
        }

        private fun getStartOfMonth(): Long {
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
                calendar.set(java.util.Calendar.MINUTE, 0)
                calendar.set(java.util.Calendar.SECOND, 0)
                calendar.set(java.util.Calendar.MILLISECOND, 0)
                return calendar.timeInMillis
        }

        private val now = System.currentTimeMillis()
        private val startOfWeek = getStartOfWeek()
        private val startOfMonth = getStartOfMonth()

        val statisticsState: StateFlow<StatisticsState> =
                combine(
                                // Total Stats
                                runDao.getTotalDistance(),
                                runDao.getTotalTimeInMillis(),
                                runDao.getTotalCaloriesBurned(),
                                runDao.getTotalAvgSpeed(),
                                // Weekly Stats
                                runDao.getTotalDistanceInRange(startOfWeek, now),
                                runDao.getTotalTimeInRange(startOfWeek, now),
                                runDao.getTotalCaloriesInRange(startOfWeek, now),
                                runDao.getAvgSpeedInRange(startOfWeek, now),
                                // Monthly Stats
                                runDao.getTotalDistanceInRange(startOfMonth, now),
                                runDao.getTotalTimeInRange(startOfMonth, now),
                                runDao.getTotalCaloriesInRange(startOfMonth, now),
                                runDao.getAvgSpeedInRange(startOfMonth, now)
                        ) { stats ->
                                StatisticsState(
                                        total =
                                                StatisticsSummary(
                                                        totalDistance = stats[0] as Int? ?: 0,
                                                        totalTime = stats[1] as Long? ?: 0L,
                                                        totalCalories = stats[2] as Int? ?: 0,
                                                        avgSpeed = stats[3] as Float? ?: 0f
                                                ),
                                        weekly =
                                                StatisticsSummary(
                                                        totalDistance = stats[4] as Int? ?: 0,
                                                        totalTime = stats[5] as Long? ?: 0L,
                                                        totalCalories = stats[6] as Int? ?: 0,
                                                        avgSpeed = stats[7] as Float? ?: 0f
                                                ),
                                        monthly =
                                                StatisticsSummary(
                                                        totalDistance = stats[8] as Int? ?: 0,
                                                        totalTime = stats[9] as Long? ?: 0L,
                                                        totalCalories = stats[10] as Int? ?: 0,
                                                        avgSpeed = stats[11] as Float? ?: 0f
                                                )
                                )
                        }
                        .stateIn(
                                scope = viewModelScope,
                                started = SharingStarted.WhileSubscribed(5000),
                                initialValue = StatisticsState()
                        )
}
