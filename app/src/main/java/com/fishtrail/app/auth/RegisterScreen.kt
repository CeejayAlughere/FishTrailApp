package com.fishtrail.app.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.ui.res.painterResource
import com.fishtrail.app.R
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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

    fun handleRegister() {
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
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Header Branding Section
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fishing_hook),
                    contentDescription = "FishTrail Logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Join FishTrail",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Create your account to start logging catches",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Registration Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Full Name Field
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            fullNameTouched = true
                        },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = fullNameError != null,
                        supportingText = {
                            if (fullNameError != null) {
                                Text(
                                    text = fullNameError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailTouched = true
                        },
                        label = { Text("Email") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(
                                    text = emailError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Username Field
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameTouched = true
                        },
                        label = { Text("Username") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        isError = usernameError != null,
                        supportingText = {
                            if (usernameError != null) {
                                Text(
                                    text = usernameError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordTouched = true
                        },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        isError = passwordError != null,
                        supportingText = {
                            if (passwordError != null) {
                                Text(
                                    text = passwordError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else {
                                Text(
                                    text = "Must be ≥ 6 chars with uppercase, lowercase, number & symbol.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordTouched = true
                        },
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        isError = confirmPasswordError != null,
                        supportingText = {
                            if (confirmPasswordError != null) {
                                Text(
                                    text = confirmPasswordError,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleRegister() }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Animated Alert Message
                    AnimatedVisibility(
                        visible = message.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isSuccess) Icons.Filled.CheckCircleOutline else Icons.Filled.ErrorOutline,
                                    contentDescription = null,
                                    tint = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (isSuccess) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    // Register Action Button
                    Button(
                        onClick = { handleRegister() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Register Account",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    // Back to Login Button
                    OutlinedButton(
                        onClick = onLoginClick,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Back to Login",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}
