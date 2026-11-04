
package com.fishtrail.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CatchDao {

    @Insert
    suspend fun insertCatch(catch: Catch)

    // Automatically emits updated catches when Room data changes.
    @Query("SELECT * FROM catches WHERE username = :username ORDER BY id DESC")
    fun getCatchesByUsername(username: String): Flow<List<Catch>>

    @Query("""
        SELECT * FROM catches
        WHERE username = :username
        AND syncStatus = 'PENDING'
    """)
    suspend fun getPendingCatches(username: String): List<Catch>

    @Query("""
        UPDATE catches
        SET syncStatus = 'SYNCED'
        WHERE id = :catchId
    """)
    suspend fun markCatchAsSynced(catchId: Int)

    @Query("""
        SELECT COUNT(*) FROM catches
        WHERE username = :username
        AND syncStatus = 'PENDING'
    """)
    suspend fun getPendingCatchCount(username: String): Int
}
