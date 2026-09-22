package com.fishtrail.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CatchDao {

    @Insert
    suspend fun insertCatch(catch: Catch)

    @Query("SELECT * FROM catches WHERE username = :username ORDER BY id DESC")
    suspend fun getCatchesByUsername(username: String): List<Catch>
}