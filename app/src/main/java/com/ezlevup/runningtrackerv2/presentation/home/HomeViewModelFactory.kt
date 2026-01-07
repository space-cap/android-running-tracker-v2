package com.ezlevup.runningtrackerv2.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ezlevup.runningtrackerv2.data.RunDao

class HomeViewModelFactory(private val runDao: RunDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return HomeViewModel(runDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
