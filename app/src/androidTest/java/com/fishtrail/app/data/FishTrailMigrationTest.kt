package com.fishtrail.app.data

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FishTrailMigrationTest {

    private val testDatabase = "fishtrail_migration_test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        FishTrailDatabase::class.java
    )

    @Test
    fun migration9To10PreservesExistingCatches() {

        val db = helper.createDatabase(testDatabase, 9)

        db.execSQL(
            """
            INSERT INTO catches (
                id, username, species, weight, length,
                bait, notes, date, time, locationName,
                latitude, longitude, syncStatus
            ) VALUES (
                16, 'testuser', 'Bass', 2.5, 45.0,
                'Spinnerbait', 'Existing synced catch',
                '18/11/2026', '12:30', 'Vaal River',
                -26.9, 28.1, 'SYNCED'
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO catches (
                id, username, species, weight, length,
                bait, notes, date, time, locationName,
                latitude, longitude, syncStatus
            ) VALUES (
                17, 'testuser', 'Trout', 1.8, 35.0,
                'Worm', 'Existing pending catch',
                '18/11/2026', '13:30', 'Fishing Lake',
                NULL, NULL, 'PENDING'
            )
            """.trimIndent()
        )

        db.close()

        val migratedDb = helper.runMigrationsAndValidate(
            testDatabase,
            10,
            true,
            FishTrailDatabase.MIGRATION_9_10
        )

        migratedDb.query(
            """
            SELECT id, username, species, weight, length,
                   bait, notes, date, time, locationName,
                   latitude, longitude, syncStatus, cloudId
            FROM catches
            ORDER BY id
            """.trimIndent()
        ).use { cursor ->

            assertEquals(2, cursor.count)

            assertTrue(cursor.moveToFirst())
            assertEquals(16, cursor.getInt(0))
            assertEquals("testuser", cursor.getString(1))
            assertEquals("Bass", cursor.getString(2))
            assertEquals(2.5, cursor.getDouble(3), 0.001)
            assertEquals(45.0, cursor.getDouble(4), 0.001)
            assertEquals("Spinnerbait", cursor.getString(5))
            assertEquals("Existing synced catch", cursor.getString(6))
            assertEquals("18/11/2026", cursor.getString(7))
            assertEquals("12:30", cursor.getString(8))
            assertEquals("Vaal River", cursor.getString(9))
            assertEquals(-26.9, cursor.getDouble(10), 0.001)
            assertEquals(28.1, cursor.getDouble(11), 0.001)
            assertEquals("SYNCED", cursor.getString(12))
            assertEquals("16", cursor.getString(13))

            assertTrue(cursor.moveToNext())
            assertEquals(17, cursor.getInt(0))
            assertEquals("testuser", cursor.getString(1))
            assertEquals("Trout", cursor.getString(2))
            assertEquals(1.8, cursor.getDouble(3), 0.001)
            assertEquals(35.0, cursor.getDouble(4), 0.001)
            assertEquals("Worm", cursor.getString(5))
            assertEquals("Existing pending catch", cursor.getString(6))
            assertEquals("18/11/2026", cursor.getString(7))
            assertEquals("13:30", cursor.getString(8))
            assertEquals("Fishing Lake", cursor.getString(9))
            assertTrue(cursor.isNull(10))
            assertTrue(cursor.isNull(11))
            assertEquals("PENDING", cursor.getString(12))
            assertEquals("17", cursor.getString(13))

            assertTrue(!cursor.moveToNext())
        }

        migratedDb.close()
    }
}
