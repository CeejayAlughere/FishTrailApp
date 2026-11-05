package com.fishtrail.app.notifications

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import java.security.MessageDigest

object FcmTokenManager {

    private const val TAG = "FishTrailFCM"

    private val firestore = FirebaseFirestore.getInstance()

    // Get and register this device's current FCM token.
    fun registerCurrentDevice(uid: String) {

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->

                saveToken(uid, token)

            }
            .addOnFailureListener { exception ->

                Log.e(
                    TAG,
                    "Unable to retrieve FCM token.",
                    exception
                )
            }
    }

    // Save or update a device token under the user's profile.
    fun saveToken(uid: String, token: String) {

        if (uid.isBlank() || token.isBlank()) {
            return
        }

        val tokenDocumentId = hashToken(token)

        val tokenData = hashMapOf(
            "token" to token,
            "platform" to "android",
            "updatedAt" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(uid)
            .collection("fcmTokens")
            .document(tokenDocumentId)
            .set(tokenData, SetOptions.merge())
            .addOnSuccessListener {

                Log.d(TAG, "FCM token registered successfully.")

            }
            .addOnFailureListener { exception ->

                Log.e(
                    TAG,
                    "Unable to save FCM token.",
                    exception
                )
            }
    }

    // Remove this device's token before signing out.
    fun removeCurrentDevice(
        uid: String,
        onComplete: () -> Unit
    ) {

        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->

                val tokenDocumentId = hashToken(token)

                firestore.collection("users")
                    .document(uid)
                    .collection("fcmTokens")
                    .document(tokenDocumentId)
                    .delete()
                    .addOnCompleteListener {

                        onComplete()
                    }
            }
            .addOnFailureListener { exception ->

                Log.e(
                    TAG,
                    "Unable to retrieve FCM token during logout.",
                    exception
                )

                onComplete()
            }
    }

    // Hash the token to create a safe Firestore document ID.
    private fun hashToken(token: String): String {

        val digest = MessageDigest
            .getInstance("SHA-256")
            .digest(token.toByteArray(Charsets.UTF_8))

        return digest.joinToString("") {
            "%02x".format(it.toInt() and 0xff)
        }
    }
}