package com.fishtrail.app.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class CatchSyncScheduler(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleSync() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest =
            OneTimeWorkRequestBuilder<CatchSyncWorker>()
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniqueWork(
            "catch_sync_work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }
}