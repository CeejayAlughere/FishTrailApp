package com.fishtrail.app.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CatchSyncSchedulerTest {

    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()

        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .setTaskExecutor(SynchronousExecutor())
            .setWorkerCoroutineContext(
                kotlinx.coroutines.Dispatchers.Unconfined
            )
            .build()

        WorkManagerTestInitHelper.initializeTestWorkManager(
            context,
            config
        )

        workManager = WorkManager.getInstance(context)
    }

    @After
    fun tearDown() {
        workManager.cancelAllWork()
            .result
            .get(10, TimeUnit.SECONDS)

        WorkManagerTestInitHelper.closeWorkDatabase()
    }

    @Test
    fun scheduleSyncCreatesWork() {
        CatchSyncScheduler(context).scheduleSync()

        val work = workManager
            .getWorkInfosForUniqueWork("catch_sync_work")
            .get(10, TimeUnit.SECONDS)

        assertEquals(1, work.size)
        assertFalse(work[0].state == WorkInfo.State.CANCELLED)
    }

    @Test
    fun scheduleSyncDoesNotCreateDuplicateWork() {
        val scheduler = CatchSyncScheduler(context)

        scheduler.scheduleSync()
        scheduler.scheduleSync()

        val work = workManager
            .getWorkInfosForUniqueWork("catch_sync_work")
            .get(10, TimeUnit.SECONDS)

        assertEquals(1, work.size)
    }
}
