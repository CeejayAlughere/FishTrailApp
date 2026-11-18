package com.fishtrail.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CatchTest {

    private fun createCatch() = Catch(
        username = "testuser",
        species = "Bass",
        weight = 2.5,
        length = 45.0,
        bait = "Spinnerbait",
        notes = "Test catch",
        date = "18/11/2026",
        time = "12:30",
        locationName = "Vaal River",
        latitude = -26.9,
        longitude = 28.1
    )

    @Test
    fun newCatchHasPendingSyncStatus() {
        val catch = createCatch()

        assertEquals("PENDING", catch.syncStatus)
    }

    @Test
    fun newCatchGeneratesCloudId() {
        val catch = createCatch()

        assertFalse(catch.cloudId.isBlank())
    }

    @Test
    fun differentCatchesHaveDifferentCloudIds() {
        val firstCatch = createCatch()
        val secondCatch = createCatch()

        assertNotEquals(
            firstCatch.cloudId,
            secondCatch.cloudId
        )
    }

    @Test
    fun newCatchHasDefaultLocalId() {
        val catch = createCatch()

        assertEquals(0, catch.id)
    }
}
