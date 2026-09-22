package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishtrail.app.data.User
import com.fishtrail.app.data.UserDao
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userDao: UserDao
) : ViewModel() {

    fun registerUser(
        fullName: String,
        email: String,
        username: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        viewModelScope.launch {

            val existingUser = userDao.getUserByUsername(username)

            if (existingUser != null) {
                onResult(false, "Username already exists.")
                return@launch
            }

            val user = User(
                fullName = fullName.trim(),
                email = email.trim().lowercase(),
                username = username.trim(),
                password = password
            )

            userDao.insertUser(user)

            onResult(true, "Registration successful.")
        }
    }

    fun loginUser(
        username: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {

        viewModelScope.launch {

            val user = userDao.getUserByUsername(username)

            if (user == null) {
                onResult(false, "Username not found.")
                return@launch
            }

            if (user.password != password) {
                onResult(false, "Incorrect password.")
                return@launch
            }

            onResult(true, "Login successful.")
        }
    }

    fun getUserByUsername(
        username: String,
        onResult: (User?) -> Unit
    ) {

        viewModelScope.launch {

            val user = userDao.getUserByUsername(username)

            onResult(user)
        }
    }
}