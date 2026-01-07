package com.ezlevup.runningtrackerv2.presentation.runlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezlevup.runningtrackerv2.data.RunDao
import com.ezlevup.runningtrackerv2.data.RunRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RunListViewModel(private val runDao: RunDao) : ViewModel() {

    // Fetch all runs sorted by date
    val runs: StateFlow<List<RunRecord>> =
            runDao.getAllRunsSortedByDate()
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = emptyList()
                    )
}
