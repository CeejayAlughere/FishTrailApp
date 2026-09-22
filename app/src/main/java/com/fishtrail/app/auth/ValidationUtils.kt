package com.fishtrail.app.auth

object ValidationUtils {

    fun validateFullName(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            return "Full Name is required."
        }
        // Should contain letters and spaces only. No numbers or special symbols.
        val regex = Regex("^[a-zA-Z\\s]+$")
        if (!regex.matches(trimmed)) {
            return "Full Name must contain only letters and spaces."
        }
        return null
    }

    fun validateUsername(username: String): String? {
        if (username.isEmpty()) {
            return "Username is required."
        }
        if (username.contains(" ")) {
            return "Username must not contain spaces."
        }
        // Allow letters, numbers, and underscore
        val regex = Regex("^[a-zA-Z0-9_]+$")
        if (!regex.matches(username)) {
            return "Username can only contain letters, numbers, and underscores."
        }
        if (username.length < 3 || username.length > 20) {
            return "Username must be between 3 and 20 characters."
        }
        return null
    }

    fun validateEmail(email: String): String? {
        if (email.isEmpty()) {
            return "Email address is required."
        }
        if (email.contains(" ")) {
            return "Email must not contain spaces."
        }
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        if (!emailRegex.matches(email)) {
            return "Invalid email format."
        }
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isEmpty()) {
            return "Password is required."
        }
        if (password.contains(" ")) {
            return "Password must not contain spaces."
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters long."
        }
        if (!password.any { it.isUpperCase() }) {
            return "Password must contain at least one uppercase letter."
        }
        if (!password.any { it.isLowerCase() }) {
            return "Password must contain at least one lowercase letter."
        }
        if (!password.any { it.isDigit() }) {
            return "Password must contain at least one number."
        }
        val specialChars = "~!@#$%^&*()_+`-={}|[]\\:\";'<>?,./"
        if (!password.any { it in specialChars }) {
            return "Password must contain at least one special character."
        }
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isEmpty()) {
            return "Confirm Password is required."
        }
        if (password != confirm) {
            return "Passwords do not match."
        }
        return null
    }
}
