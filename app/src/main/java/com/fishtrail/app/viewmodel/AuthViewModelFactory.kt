package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fishtrail.app.data.UserDao

class AuthViewModelFactory(
    private val userDao: UserDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(userDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}