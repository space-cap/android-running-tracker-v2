package com.ezlevup.runningtrackerv2.presentation.rundetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ezlevup.runningtrackerv2.data.RunDao
import com.ezlevup.runningtrackerv2.data.RunRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RunDetailViewModel(private val runDao: RunDao, private val runId: Int) : ViewModel() {

    val run: StateFlow<RunRecord?> =
            runDao.getRunById(runId)
                    .stateIn(
                            scope = viewModelScope,
                            started = SharingStarted.WhileSubscribed(5000),
                            initialValue = null
                    )
}
