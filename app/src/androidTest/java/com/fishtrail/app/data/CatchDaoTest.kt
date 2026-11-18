package com.fishtrail.app.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatchDaoTest {

    private lateinit var database: FishTrailDatabase
    private lateinit var catchDao: CatchDao

    @Before
    fun setup() {
        val context = ApplicationProvider
            .getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            FishTrailDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        catchDao = database.catchDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createCatch(
        username: String = "testuser",
        species: String = "Bass",
        syncStatus: String = "PENDING"
    ) = Catch(
        username = username,
        species = species,
        weight = 2.5,
        length = 45.0,
        bait = "Spinnerbait",
        notes = "Automated test",
        date = "18/11/2026",
        time = "12:30",
        locationName = "Vaal River",
        latitude = -26.9,
        longitude = 28.1,
        syncStatus = syncStatus
    )

    @Test
    fun insertCatchStoresRecord() = runBlocking {
        catchDao.insertCatch(createCatch())

        val catches = catchDao
            .getCatchesByUsername("testuser")
            .first()

        assertEquals(1, catches.size)
        assertEquals("Bass", catches[0].species)
        assertEquals("PENDING", catches[0].syncStatus)
    }

    @Test
    fun pendingQueryExcludesSyncedCatches() = runBlocking {
        catchDao.insertCatch(createCatch(species = "Bass"))
        catchDao.insertCatch(
            createCatch(
                species = "Trout",
                syncStatus = "SYNCED"
            )
        )

        val pending = catchDao.getPendingCatches("testuser")

        assertEquals(1, pending.size)
        assertEquals("Bass", pending[0].species)
    }

    @Test
    fun markCatchAsSyncedUpdatesStatus() = runBlocking {
        catchDao.insertCatch(createCatch())

        val catch = catchDao
            .getPendingCatches("testuser")
            .first()

        catchDao.markCatchAsSynced(catch.id)

        val pending = catchDao.getPendingCatches("testuser")

        val updated = catchDao
            .getCatchesByUsername("testuser")
            .first()

        assertTrue(pending.isEmpty())
        assertEquals("SYNCED", updated.first().syncStatus)
    }

    @Test
    fun pendingCountIsAccurate() = runBlocking {
        catchDao.insertCatch(createCatch(species = "Bass"))
        catchDao.insertCatch(createCatch(species = "Carp"))
        catchDao.insertCatch(
            createCatch(
                species = "Trout",
                syncStatus = "SYNCED"
            )
        )

        assertEquals(
            2,
            catchDao.getPendingCatchCount("testuser")
        )
    }

    @Test
    fun catchesAreSeparatedByUsername() = runBlocking {
        catchDao.insertCatch(
            createCatch(username = "user_one")
        )

        catchDao.insertCatch(
            createCatch(
                username = "user_two",
                species = "Trout"
            )
        )

        val userOneCatches = catchDao
            .getCatchesByUsername("user_one")
            .first()

        val userTwoCatches = catchDao
            .getCatchesByUsername("user_two")
            .first()

        assertEquals(1, userOneCatches.size)
        assertEquals("Bass", userOneCatches[0].species)

        assertEquals(1, userTwoCatches.size)
        assertEquals("Trout", userTwoCatches[0].species)

        assertEquals(
            1,
            catchDao.getPendingCatchCount("user_one")
        )
    }
}
