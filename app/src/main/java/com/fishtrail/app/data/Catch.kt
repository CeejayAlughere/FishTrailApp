package com.fishtrail.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "catches")
data class Catch(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val username: String,

    val species: String,

    val weight: Double,

    val length: Double,

    val bait: String,

    val notes: String,

    val date: String,

    val time: String,

    val locationName: String,

    val latitude: Double?,

    val longitude: Double?
)