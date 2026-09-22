package com.fishtrail.app.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.fishtrail.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onRegisterClick: () -> Unit
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var usernameTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var submitAttempted by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val usernameError = if (usernameTouched || submitAttempted) {
        if (username.trim().isEmpty()) "Username is required." else null
    } else null

    val passwordError = if (passwordTouched || submitAttempted) {
        if (password.isEmpty()) "Password is required." else null
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "FishTrail",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                usernameTouched = true
            },
            label = { Text("Username") },
            isError = usernameError != null,
            supportingText = {
                if (usernameError != null) {
                    Text(text = usernameError, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordTouched = true
            },
            label = { Text("Password") },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(text = passwordError, color = MaterialTheme.colorScheme.error)
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = if (isSuccess) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                submitAttempted = true

                if (username.trim().isNotEmpty() && password.isNotEmpty()) {
                    authViewModel.loginUser(
                        username = username.trim(),
                        password = password
                    ) { success, resultMessage ->
                        message = resultMessage
                        isSuccess = success

                        if (success) {
                            onLoginSuccess(username.trim())
                        }
                    }
                } else {
                    message = "Please fill in all required fields."
                    isSuccess = false
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Login")
        }

        Button(
            onClick = onRegisterClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Create Account")
        }
    }
}