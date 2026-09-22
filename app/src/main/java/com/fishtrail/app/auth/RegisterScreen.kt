package com.fishtrail.app.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
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
fun RegisterScreen(
    authViewModel: AuthViewModel,
    onLoginClick: () -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var fullNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var usernameTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    var submitAttempted by remember { mutableStateOf(false) }

    var message by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }

    val fullNameError = if (fullNameTouched || submitAttempted) ValidationUtils.validateFullName(fullName) else null
    val emailError = if (emailTouched || submitAttempted) ValidationUtils.validateEmail(email) else null
    val usernameError = if (usernameTouched || submitAttempted) ValidationUtils.validateUsername(username) else null
    val passwordError = if (passwordTouched || submitAttempted) ValidationUtils.validatePassword(password) else null
    val confirmPasswordError = if (confirmPasswordTouched || submitAttempted) ValidationUtils.validateConfirmPassword(password, confirmPassword) else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Create FishTrail Account",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = {
                fullName = it
                fullNameTouched = true
            },
            label = { Text("Full Name") },
            isError = fullNameError != null,
            supportingText = {
                if (fullNameError != null) {
                    Text(text = fullNameError, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailTouched = true
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(text = emailError, color = MaterialTheme.colorScheme.error)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
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
                } else {
                    Text(text = "Must be ≥ 6 chars with uppercase, lowercase, number & symbol.")
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                confirmPasswordTouched = true
            },
            label = { Text("Confirm Password") },
            isError = confirmPasswordError != null,
            supportingText = {
                if (confirmPasswordError != null) {
                    Text(text = confirmPasswordError, color = MaterialTheme.colorScheme.error)
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
                
                val nameValid = ValidationUtils.validateFullName(fullName) == null
                val emailValid = ValidationUtils.validateEmail(email) == null
                val userValid = ValidationUtils.validateUsername(username) == null
                val passValid = ValidationUtils.validatePassword(password) == null
                val confirmValid = ValidationUtils.validateConfirmPassword(password, confirmPassword) == null

                if (nameValid && emailValid && userValid && passValid && confirmValid) {
                    authViewModel.registerUser(
                        fullName = fullName.trim(),
                        email = email.trim().lowercase(),
                        username = username.trim(),
                        password = password
                    ) { success, resultMessage ->
                        message = resultMessage
                        isSuccess = success

                        if (success) {
                            fullName = ""
                            email = ""
                            username = ""
                            password = ""
                            confirmPassword = ""
                            fullNameTouched = false
                            emailTouched = false
                            usernameTouched = false
                            passwordTouched = false
                            confirmPasswordTouched = false
                            submitAttempted = false
                        }
                    }
                } else {
                    message = "Please correct the errors above."
                    isSuccess = false
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Register")
        }

        Button(
            onClick = onLoginClick,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Back to Login")
        }
    }
}