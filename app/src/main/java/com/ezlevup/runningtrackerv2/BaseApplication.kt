package com.ezlevup.runningtrackerv2

import android.app.Application
import com.ezlevup.runningtrackerv2.data.RunningDatabase

class BaseApplication : Application() {
    val database: RunningDatabase by lazy { RunningDatabase.getInstance(this) }
}
