package com.ezlevup.runningtrackerv2.presentation.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ezlevup.runningtrackerv2.data.RunDao

class StatisticsViewModelFactory(private val runDao: RunDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return StatisticsViewModel(runDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
