package com.fishtrail.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fishtrail.app.data.CatchDao
import com.fishtrail.app.sync.CatchSyncScheduler

class CatchViewModelFactory(
    private val catchDao: CatchDao,
    context: Context
) : ViewModelProvider.Factory {

    private val syncScheduler = CatchSyncScheduler(context.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(CatchViewModel::class.java)) {
            return CatchViewModel(
                catchDao,
                syncScheduler
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}