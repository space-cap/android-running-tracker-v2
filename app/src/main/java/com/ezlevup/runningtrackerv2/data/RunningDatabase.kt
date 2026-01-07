package com.ezlevup.runningtrackerv2.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [RunRecord::class], version = 1)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {

    abstract fun getRunDao(): RunDao

    companion object {
        @Volatile private var instance: RunningDatabase? = null

        fun getInstance(context: android.content.Context): RunningDatabase {
            return instance
                    ?: synchronized(this) {
                        instance
                                ?: androidx.room.Room.databaseBuilder(
                                                context.applicationContext,
                                                RunningDatabase::class.java,
                                                "running_db"
                                        )
                                        .build()
                                        .also { instance = it }
                    }
        }
    }
}
