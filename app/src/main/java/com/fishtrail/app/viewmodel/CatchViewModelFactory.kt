package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fishtrail.app.data.CatchDao

class CatchViewModelFactory(
    private val catchDao: CatchDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(CatchViewModel::class.java)) {
            return CatchViewModel(catchDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}