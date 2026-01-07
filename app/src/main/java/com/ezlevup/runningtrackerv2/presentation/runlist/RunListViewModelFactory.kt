package com.ezlevup.runningtrackerv2.presentation.runlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ezlevup.runningtrackerv2.data.RunDao

class RunListViewModelFactory(private val runDao: RunDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return RunListViewModel(runDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
