package com.fishtrail.app.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishtrail.app.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // =========================================================
    // NORMAL REGISTRATION
    // =========================================================

    fun registerUser(
        fullName: String,
        email: String,
        username: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanFullName = fullName.trim()
        val cleanEmail = email.trim().lowercase()
        val cleanUsername = username.trim().lowercase()

        if (cleanUsername.isBlank()) {
            onResult(false, "Please enter a username.")
            return
        }

        auth.createUserWithEmailAndPassword(
            cleanEmail,
            password
        )
            .addOnSuccessListener { authResult ->

                val firebaseUser = authResult.user

                if (firebaseUser == null) {
                    onResult(
                        false,
                        "Account could not be created."
                    )
                    return@addOnSuccessListener
                }

                reserveUsernameAndCreateProfile(
                    uid = firebaseUser.uid,
                    fullName = cleanFullName,
                    email = cleanEmail,
                    username = cleanUsername,
                    onResult = { success: Boolean, message: String ->

                        if (success) {
                            onResult(
                                true,
                                "Registration successful."
                            )
                        } else {

                            firebaseUser.delete()

                            onResult(
                                false,
                                message
                            )
                        }
                    }
                )
            }
            .addOnFailureListener { exception ->
                onResult(
                    false,
                    getFirebaseErrorMessage(exception)
                )
            }
    }

    // =========================================================
    // RESERVE USERNAME + CREATE PROFILE
    // =========================================================

    private fun reserveUsernameAndCreateProfile(
        uid: String,
        fullName: String,
        email: String,
        username: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val usernameDocument =
            firestore.collection("usernames")
                .document(username)

        val userDocument =
            firestore.collection("users")
                .document(uid)

        firestore.runTransaction { transaction ->

            val usernameSnapshot =
                transaction.get(usernameDocument)

            if (usernameSnapshot.exists()) {
                throw UsernameAlreadyTakenException()
            }

            val usernameReservation =
                hashMapOf(
                    "uid" to uid,
                    "username" to username
                )

            val userProfile =
                hashMapOf(
                    "uid" to uid,
                    "fullName" to fullName,
                    "email" to email,
                    "username" to username
                )

            transaction.set(
                usernameDocument,
                usernameReservation
            )

            transaction.set(
                userDocument,
                userProfile
            )

            null
        }
            .addOnSuccessListener {
                onResult(
                    true,
                    "Registration successful."
                )
            }
            .addOnFailureListener { exception ->

                if (exception is UsernameAlreadyTakenException) {

                    onResult(
                        false,
                        "Username already exists. Please choose another username."
                    )

                } else {

                    onResult(
                        false,
                        "Your account could not be created. Please try again."
                    )
                }
            }
    }
    // =========================================================
// EMAIL / PASSWORD LOGIN
// =========================================================

    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        val cleanEmail = email.trim().lowercase()

        auth.signInWithEmailAndPassword(
            cleanEmail,
            password
        )
            .addOnSuccessListener { authResult ->

                val uid = authResult.user?.uid

                if (uid.isNullOrBlank()) {
                    onResult(
                        false,
                        "Login succeeded, but the user account could not be identified.",
                        null
                    )
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { document ->

                        if (!document.exists()) {
                            onResult(
                                false,
                                "Login succeeded, but your FishTrail profile could not be found.",
                                null
                            )
                            return@addOnSuccessListener
                        }

                        val username = document
                            .getString("username")
                            ?.trim()
                            ?.lowercase()

                        if (username.isNullOrBlank()) {
                            onResult(
                                false,
                                "Login succeeded, but your username could not be found.",
                                null
                            )
                            return@addOnSuccessListener
                        }

                        onResult(
                            true,
                            "Login successful.",
                            username
                        )
                    }
                    .addOnFailureListener { exception ->

                        onResult(
                            false,
                            "Login succeeded, but your profile could not be loaded: ${
                                exception.message ?: "Unknown error"
                            }",
                            null
                        )
                    }
            }
            .addOnFailureListener { exception ->

                onResult(
                    false,
                    getFirebaseErrorMessage(exception),
                    null
                )
            }
    }

    // =========================================================
    // GOOGLE SIGN-IN
    // =========================================================

    fun signInWithGoogle(
        context: Context,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        viewModelScope.launch {

            try {

                val credentialManager =
                    CredentialManager.create(context)

                val googleIdOption =
                    GetGoogleIdOption.Builder()
                        .setServerClientId(
                            context.getString(
                                R.string.default_web_client_id
                            )
                        )
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()

                val request =
                    GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                val result =
                    credentialManager.getCredential(
                        context = context,
                        request = request
                    )

                val credential =
                    result.credential

                if (
                    credential is CustomCredential &&
                    credential.type ==
                    GoogleIdTokenCredential
                        .TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {

                    val googleCredential =
                        GoogleIdTokenCredential
                            .createFrom(
                                credential.data
                            )

                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(
                            googleCredential.idToken,
                            null
                        )

                    auth.signInWithCredential(
                        firebaseCredential
                    )
                        .addOnSuccessListener { authResult ->

                            val firebaseUser =
                                authResult.user

                            if (firebaseUser == null) {

                                onResult(
                                    false,
                                    "Google sign-in failed.",
                                    null
                                )

                                return@addOnSuccessListener
                            }

                            handleGoogleUser(
                                uid = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                fullName =
                                    firebaseUser.displayName
                                        ?: firebaseUser.email
                                            ?.substringBefore("@")
                                        ?: "FishTrail User",
                                onResult = onResult
                            )
                        }
                        .addOnFailureListener { exception ->

                            onResult(
                                false,
                                getFirebaseErrorMessage(
                                    exception
                                ),
                                null
                            )
                        }

                } else {

                    onResult(
                        false,
                        "Google sign-in credential was not recognised.",
                        null
                    )
                }

            } catch (exception: Exception) {

                onResult(
                    false,
                    exception.message
                        ?: "Google sign-in was cancelled or could not be completed.",
                    null
                )
            }
        }
    }

    // =========================================================
    // HANDLE GOOGLE USER
    // =========================================================

    private fun handleGoogleUser(
        uid: String,
        email: String,
        fullName: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {

        val cleanEmail =
            email.trim().lowercase()

        val userDocument =
            firestore.collection("users")
                .document(uid)

        userDocument.get()
            .addOnSuccessListener { document ->

                // Existing FishTrail Google user
                if (document.exists()) {

                    val existingEmail = document.getString("email") ?: ""
                    val existingFullName = document.getString("fullName") ?: ""

                    val updates = mutableMapOf<String, Any>()
                    if (existingEmail.isBlank() && cleanEmail.isNotBlank()) {
                        updates["email"] = cleanEmail
                    }
                    if (existingFullName.isBlank() && fullName.isNotBlank()) {
                        updates["fullName"] = fullName.trim()
                    }
                    if (updates.isNotEmpty()) {
                        userDocument.update(updates)
                    }

                    val username =
                        document.getString("username")
                            ?: generateBaseUsername(
                                cleanEmail
                            )

                    onResult(
                        true,
                        "Google sign-in successful.",
                        username
                    )

                    return@addOnSuccessListener
                }

                // New Google user
                val baseUsername =
                    generateBaseUsername(
                        cleanEmail
                    )

                createUniqueGoogleProfile(
                    uid = uid,
                    fullName = fullName,
                    email = cleanEmail,
                    baseUsername = baseUsername,
                    attempt = 0,
                    onResult = onResult
                )
            }
            .addOnFailureListener {

                onResult(
                    false,
                    "Unable to load your FishTrail profile.",
                    null
                )
            }
    }

    // =========================================================
    // CREATE UNIQUE GOOGLE USERNAME
    // =========================================================

    private fun createUniqueGoogleProfile(
        uid: String,
        fullName: String,
        email: String,
        baseUsername: String,
        attempt: Int,
        onResult: (Boolean, String, String?) -> Unit
    ) {

        val username =
            if (attempt == 0) {
                baseUsername
            } else {
                "$baseUsername${attempt + 1}"
            }

        val usernameDocument =
            firestore.collection("usernames")
                .document(username)

        val userDocument =
            firestore.collection("users")
                .document(uid)

        firestore.runTransaction { transaction ->

            val usernameSnapshot =
                transaction.get(usernameDocument)

            if (usernameSnapshot.exists()) {
                throw UsernameAlreadyTakenException()
            }

            val usernameReservation =
                hashMapOf(
                    "uid" to uid,
                    "username" to username
                )

            val userProfile =
                hashMapOf(
                    "uid" to uid,
                    "fullName" to fullName.trim(),
                    "email" to email,
                    "username" to username
                )

            transaction.set(
                usernameDocument,
                usernameReservation
            )

            transaction.set(
                userDocument,
                userProfile
            )

            null
        }
            .addOnSuccessListener {

                onResult(
                    true,
                    "Google sign-in successful.",
                    username
                )
            }
            .addOnFailureListener { exception ->

                if (
                    exception is UsernameAlreadyTakenException
                ) {

                    createUniqueGoogleProfile(
                        uid = uid,
                        fullName = fullName,
                        email = email,
                        baseUsername = baseUsername,
                        attempt = attempt + 1,
                        onResult = onResult
                    )

                } else {

                    onResult(
                        false,
                        "Google account signed in, but your FishTrail profile could not be saved.",
                        null
                    )
                }
            }
    }

    // =========================================================
    // FIND USER BY USERNAME
    // =========================================================

    fun getUserByUsername(
        username: String,
        onResult: (UserProfile?) -> Unit
    ) {

        val cleanUsername =
            username.trim().lowercase()

        firestore.collection("users")
            .whereEqualTo(
                "username",
                cleanUsername
            )
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (querySnapshot.isEmpty) {
                    onResult(null)
                    return@addOnSuccessListener
                }

                val document =
                    querySnapshot.documents.first()

                val emailFromDoc = document.getString("email") ?: ""
                val firebaseEmail = auth.currentUser?.email ?: ""
                val finalEmail = emailFromDoc.ifBlank { firebaseEmail }

                val nameFromDoc = document.getString("fullName") ?: ""
                val firebaseName = auth.currentUser?.displayName ?: ""
                val finalName = nameFromDoc.ifBlank { firebaseName }

                val profile =
                    UserProfile(
                        uid =
                            document.getString("uid")
                                ?: auth.currentUser?.uid
                                ?: "",
                        fullName = finalName,
                        email = finalEmail,
                        username =
                            document.getString("username")
                                ?: ""
                    )

                onResult(profile)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // =========================================================
    // FIND USER BY EMAIL
    // =========================================================

    fun getUserByEmail(
        email: String,
        onResult: (UserProfile?) -> Unit
    ) {

        val cleanEmail =
            email.trim().lowercase()

        firestore.collection("users")
            .whereEqualTo(
                "email",
                cleanEmail
            )
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->

                if (querySnapshot.isEmpty) {
                    onResult(null)
                    return@addOnSuccessListener
                }

                val document =
                    querySnapshot.documents.first()

                val profile =
                    UserProfile(
                        uid =
                            document.getString("uid")
                                ?: "",
                        fullName =
                            document.getString("fullName")
                                ?: "",
                        email =
                            document.getString("email")
                                ?: "",
                        username =
                            document.getString("username")
                                ?: ""
                    )

                onResult(profile)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }


    // =========================================================
    // UPDATE FULL NAME
    // =========================================================

    fun updateFullName(
        newFullName: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanFullName = newFullName.trim()
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            onResult(false, "Please log in again.")
            return
        }

        if (cleanFullName.isBlank()) {
            onResult(false, "Full name cannot be empty.")
            return
        }

        if (cleanFullName.length < 2) {
            onResult(false, "Full name must contain at least 2 characters.")
            return
        }

        firestore.collection("users")
            .document(firebaseUser.uid)
            .update("fullName", cleanFullName)
            .addOnSuccessListener {
                onResult(true, "Full name updated successfully.")
            }
            .addOnFailureListener { exception ->
                onResult(
                    false,
                    exception.message ?: "Unable to update your name."
                )
            }
    }

    // =========================================================
    // UPDATE EMAIL ADDRESS
    // =========================================================

    fun updateEmail(
        newEmail: String,
        onResult: (Boolean, String) -> Unit
    ) {
        val cleanEmail = newEmail.trim().lowercase()
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            onResult(false, "Please log in again.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(cleanEmail)
                .matches()
        ) {
            onResult(false, "Please enter a valid email address.")
            return
        }

        if (cleanEmail == firebaseUser.email?.lowercase()) {
            onResult(false, "This is already your current email address.")
            return
        }

        // Send a verification request to the new email.
        firebaseUser.verifyBeforeUpdateEmail(cleanEmail)
            .addOnSuccessListener {
                onResult(
                    true,
                    "A verification email has been sent to $cleanEmail. Verify it to complete your email change."
                )
            }
            .addOnFailureListener { exception ->
                onResult(
                    false,
                    exception.message
                        ?: "Unable to update your email. You may need to log in again."
                )
            }
    }


    // =========================================================
    // REFRESH AND SYNC VERIFIED EMAIL
    // =========================================================

    fun refreshEmailAfterVerification(
        onResult: (Boolean, String, String?) -> Unit
    ) {
        val firebaseUser = auth.currentUser

        if (firebaseUser == null) {
            onResult(false, "Please log in again.", null)
            return
        }

        firebaseUser.reload()
            .addOnSuccessListener {

                val refreshedUser = auth.currentUser
                val updatedEmail = refreshedUser?.email
                    ?.trim()
                    ?.lowercase()

                if (updatedEmail.isNullOrBlank()) {
                    onResult(
                        false,
                        "Unable to retrieve your updated email.",
                        null
                    )
                    return@addOnSuccessListener
                }

                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .update("email", updatedEmail)
                    .addOnSuccessListener {
                        onResult(
                            true,
                            "Your email address has been updated successfully.",
                            updatedEmail
                        )
                    }
                    .addOnFailureListener { exception ->
                        onResult(
                            false,
                            exception.message
                                ?: "Your email was verified, but your FishTrail profile could not be updated.",
                            null
                        )
                    }
            }
            .addOnFailureListener { exception ->
                onResult(
                    false,
                    exception.message
                        ?: "Unable to refresh your account. Please try again.",
                    null
                )
            }
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    fun logout() {
        auth.signOut()
    }

    // =========================================================
    // CHECK LOGIN STATE
    // =========================================================

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // =========================================================
    // CURRENT USER HELPERS
    // =========================================================

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email?.trim()?.lowercase() ?: ""
    }

    fun getCurrentUserDisplayName(): String {
        return auth.currentUser?.displayName?.trim() ?: ""
    }

    // =========================================================
    // GENERATE GOOGLE USERNAME
    // =========================================================

    private fun generateBaseUsername(
        email: String
    ): String {

        val emailPrefix =
            email.substringBefore("@")
                .lowercase()
                .trim()

        val cleaned =
            emailPrefix.replace(
                Regex("[^a-z0-9_]"),
                ""
            )

        return if (cleaned.isBlank()) {
            "fishtrailuser"
        } else {
            cleaned
        }
    }

    // =========================================================
    // FIREBASE ERROR HANDLING
    // =========================================================

    private fun getFirebaseErrorMessage(
        exception: Exception
    ): String {

        val message =
            exception.message ?: ""

        return when {

            message.contains(
                "email address is already in use",
                ignoreCase = true
            ) ->
                "An account with this email already exists."

            message.contains(
                "badly formatted",
                ignoreCase = true
            ) ->
                "Please enter a valid email address."

            message.contains(
                "password",
                ignoreCase = true
            ) &&
                    message.contains(
                        "invalid",
                        ignoreCase = true
                    ) ->
                "Incorrect email or password."

            message.contains(
                "no user record",
                ignoreCase = true
            ) ->
                "No account was found with this email."

            message.contains(
                "invalid credential",
                ignoreCase = true
            ) ->
                "Incorrect email or password."

            message.contains(
                "network",
                ignoreCase = true
            ) ->
                "Network error. Please check your internet connection."

            else ->
                message.ifBlank {
                    "Authentication failed. Please try again."
                }
        }
    }
}

// =============================================================
// CUSTOM EXCEPTION
// =============================================================

private class UsernameAlreadyTakenException :
    Exception()

// =============================================================
// USER PROFILE
// =============================================================

data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val username: String
)