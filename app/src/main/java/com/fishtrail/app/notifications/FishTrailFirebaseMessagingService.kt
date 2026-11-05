
package com.fishtrail.app.notifications

import com.google.firebase.auth.FirebaseAuth
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fishtrail.app.MainActivity
import com.fishtrail.app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FishTrailFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "fishtrail_notifications"
        const val CHANNEL_NAME = "FishTrail Notifications"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        android.util.Log.d(
            "FishTrailFCM",
            "New FCM token received."
        )

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (!uid.isNullOrBlank()) {
            FcmTokenManager.saveToken(uid, token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "FishTrail"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "You have a new notification."

        showNotification(title, body)
    }


    private fun showNotification(title: String, body: String) {
        createNotificationChannel()

        // Check whether notification permission is granted.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                android.util.Log.w(
                    "FishTrailFCM",
                    "Notification permission has not been granted."
                )
                return
            }
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_fishing_hook)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(this).notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Fishing alerts and updates from FishTrail"
            }

            val manager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}
