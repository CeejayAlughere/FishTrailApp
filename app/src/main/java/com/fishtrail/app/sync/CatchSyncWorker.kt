
package com.fishtrail.app.sync

import android.Manifest
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
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fishtrail.app.MainActivity
import com.fishtrail.app.R
import com.fishtrail.app.data.FishTrailDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CatchSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "CatchSyncWorker"
        private const val CHANNEL_ID = "fishtrail_notifications"
        private const val CHANNEL_NAME = "FishTrail Notifications"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {

            // Check that a Firebase user is signed in.
            val currentUser = FirebaseAuth.getInstance().currentUser
                ?: return Result.success()

            val uid = currentUser.uid

            val database = FishTrailDatabase.getDatabase(applicationContext)
            val catchDao = database.catchDao()
            val firestore = FirebaseFirestore.getInstance()

            // Retrieve the signed-in user's Firestore profile.
            val userDocument = firestore
                .collection("users")
                .document(uid)
                .get()
                .await()

            val username = userDocument.getString("username")
                ?: return Result.retry()

            // Retrieve pending catches belonging to this username.
            val pendingCatches = catchDao.getPendingCatches(username)

            // Nothing needs synchronising.
            if (pendingCatches.isEmpty()) {
                return Result.success()
            }

            // Upload each pending catch to Firestore.
            for (catch in pendingCatches) {

                // Stop if the account changes while syncing.
                if (FirebaseAuth.getInstance().currentUser?.uid != uid) {
                    return Result.success()
                }

                val catchData = hashMapOf<String, Any?>(
                    "uid" to uid,
                    "username" to catch.username,
                    "species" to catch.species,
                    "weight" to catch.weight,
                    "length" to catch.length,
                    "bait" to catch.bait,
                    "notes" to catch.notes,
                    "date" to catch.date,
                    "time" to catch.time,
                    "locationName" to catch.locationName,
                    "latitude" to catch.latitude,
                    "longitude" to catch.longitude
                )

                // Use the Room ID as the Firestore document ID.
                // This prevents duplicate documents when an upload is retried.
                firestore
                    .collection("users")
                    .document(uid)
                    .collection("catches")
                    .document(catch.id.toString())
                    .set(catchData)
                    .await()

                // Mark as synced only after Firestore confirms success.
                catchDao.markCatchAsSynced(catch.id)
            }

            // Only notify after every pending catch has synced successfully.
            if (FirebaseAuth.getInstance().currentUser?.uid == uid) {
                showSyncCompleteNotification(pendingCatches.size)
            }

            Result.success()

        } catch (exception: Exception) {

            Log.e(
                TAG,
                "Catch synchronisation failed",
                exception
            )

            Result.retry()
        }
    }

    private fun showSyncCompleteNotification(catchCount: Int) {

        createNotificationChannel()

        // Check notification permission on Android 13 and newer.
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            applicationContext.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification permission has not been granted.")
            return
        }

        if (!NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            Log.w(TAG, "Notifications are disabled.")
            return
        }

        val intent = Intent(
            applicationContext,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val message = if (catchCount == 1) {
            "Your offline catch has been saved to the cloud."
        } else {
            "$catchCount offline catches have been saved to the cloud."
        }

        val notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_fishing_hook)
            .setContentTitle("Catch synchronisation complete!")
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat
            .from(applicationContext)
            .notify(NOTIFICATION_ID, notification)

        Log.d(TAG, "Catch sync notification displayed.")
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when offline catches finish syncing."
            }

            val manager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }
}
