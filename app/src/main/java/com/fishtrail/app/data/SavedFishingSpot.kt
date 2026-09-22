package com.fishtrail.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_fishing_spots")
data class SavedFishingSpot(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val username: String,

    val name: String,

    val address: String,

    val latitude: Double,

    val longitude: Double
)