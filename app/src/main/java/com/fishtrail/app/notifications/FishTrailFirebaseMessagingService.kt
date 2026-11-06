package com.fishtrail.app.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.fishtrail.app.MainActivity
import com.fishtrail.app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FishTrailFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FishTrailFCM"

        const val CHANNEL_ID = "fishtrail_notifications"
        const val CHANNEL_NAME = "FishTrail Notifications"

        const val EXTRA_NOTIFICATION_ROUTE =
            "com.fishtrail.app.EXTRA_NOTIFICATION_ROUTE"

        const val DEFAULT_NOTIFICATION_ROUTE = "home"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "New FCM token received.")

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

        val route = message.data["route"]
            ?.takeIf { it.isNotBlank() }
            ?: message.data["screen"]
                ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_NOTIFICATION_ROUTE

        showNotification(title, body, route)
    }

    private fun showNotification(
        title: String,
        body: String,
        route: String
    ) {

        createNotificationChannel()

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification permission has not been granted.")
            return
        }

        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {

            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

            putExtra(
                EXTRA_NOTIFICATION_ROUTE,
                route
            )
        }

        val notificationId = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_fishing_hook)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(notificationId, notification)

        Log.d(TAG, "Notification displayed successfully.")
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description =
                    "Fishing alerts and updates from FishTrail"
            }

            val manager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}