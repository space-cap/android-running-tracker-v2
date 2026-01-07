package com.ezlevup.runningtrackerv2.presentation.rundetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ezlevup.runningtrackerv2.data.RunDao

class RunDetailViewModelFactory(private val runDao: RunDao, private val runId: Int) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return RunDetailViewModel(runDao, runId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
