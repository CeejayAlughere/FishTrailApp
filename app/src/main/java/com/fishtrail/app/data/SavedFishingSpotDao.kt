package com.fishtrail.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedFishingSpotDao {

    @Insert
    suspend fun insertSpot(spot: SavedFishingSpot)

    @Delete
    suspend fun deleteSpot(spot: SavedFishingSpot)

    @Query(
        "SELECT * FROM saved_fishing_spots " +
                "WHERE username = :username " +
                "ORDER BY id DESC"
    )
    fun getSavedSpotsByUsername(
        username: String
    ): Flow<List<SavedFishingSpot>>

    @Query(
        "SELECT COUNT(*) FROM saved_fishing_spots " +
                "WHERE username = :username " +
                "AND latitude = :latitude " +
                "AND longitude = :longitude"
    )
    suspend fun countSpot(
        username: String,
        latitude: Double,
        longitude: Double
    ): Int
}