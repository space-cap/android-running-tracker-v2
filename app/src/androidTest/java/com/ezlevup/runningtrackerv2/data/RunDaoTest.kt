package com.ezlevup.runningtrackerv2.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class RunDaoTest {

    private lateinit var database: RunningDatabase
    private lateinit var dao: RunDao

    @Before
    fun setup() {
        database =
                Room.inMemoryDatabaseBuilder(
                                ApplicationProvider.getApplicationContext(),
                                RunningDatabase::class.java
                        )
                        .allowMainThreadQueries()
                        .build()
        dao = database.getRunDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertRun() = runBlocking {
        val run =
                RunRecord(
                        timestamp = 123L,
                        avgSpeedInKMH = 10f,
                        distanceInMeters = 100,
                        timeInMillis = 1000L,
                        caloriesBurned = 50
                )
        dao.insertRun(run)

        val allRuns = dao.getAllRunsSortedByDate().first()
        assertThat(allRuns).contains(run)
    }
}
