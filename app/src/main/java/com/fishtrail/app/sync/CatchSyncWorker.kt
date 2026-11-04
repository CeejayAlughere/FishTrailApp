
package com.fishtrail.app.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fishtrail.app.data.FishTrailDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CatchSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

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

            // Only retrieve pending catches belonging to this username.
            val pendingCatches = catchDao.getPendingCatches(username)

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

            Result.success()

        } catch (exception: Exception) {

            Log.e(
                "CatchSyncWorker",
                "Catch synchronisation failed",
                exception
            )

            Result.retry()
        }
    }
}
