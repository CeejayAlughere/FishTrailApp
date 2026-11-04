
package com.fishtrail.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        User::class,
        Catch::class
    ],
    version = 9,
    exportSchema = false
)
abstract class FishTrailDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun catchDao(): CatchDao

    companion object {

        @Volatile
        private var INSTANCE: FishTrailDatabase? = null

        private val MIGRATION_6_7 =
            object : Migration(6, 7) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS saved_fishing_spots (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            username TEXT NOT NULL,
                            name TEXT NOT NULL,
                            address TEXT NOT NULL,
                            latitude REAL NOT NULL,
                            longitude REAL NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_7_8 =
            object : Migration(7, 8) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "DROP TABLE IF EXISTS saved_fishing_spots"
                    )
                }
            }

        // Version 8 to 9: Add catch synchronisation status.
        private val MIGRATION_8_9 =
            object : Migration(8, 9) {
                override fun migrate(database: SupportSQLiteDatabase) {

                    database.execSQL(
                        """
                        ALTER TABLE catches
                        ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'PENDING'
                        """.trimIndent()
                    )
                }
            }

        fun getDatabase(context: Context): FishTrailDatabase {

            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FishTrailDatabase::class.java,
                    "fishtrail_database"
                )
                    .addMigrations(
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
