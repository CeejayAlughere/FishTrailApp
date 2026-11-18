
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
    version = 10,
    exportSchema = true
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



        internal val MIGRATION_9_10 =
            object : Migration(9, 10) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                ALTER TABLE catches
                ADD COLUMN cloudId TEXT NOT NULL DEFAULT ''
                """.trimIndent()
                    )

                    // Preserve the original Firestore document IDs
                    // for all catches created before version 10.
                    database.execSQL(
                        """
                UPDATE catches
                SET cloudId = CAST(id AS TEXT)
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
                        MIGRATION_8_9,
                        MIGRATION_9_10
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
