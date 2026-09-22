package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishtrail.app.data.Catch
import com.fishtrail.app.data.CatchDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatchViewModel(
    private val catchDao: CatchDao
) : ViewModel() {

    private val _catches = MutableStateFlow<List<Catch>>(emptyList())

    val catches: StateFlow<List<Catch>> = _catches.asStateFlow()

    fun saveCatch(
        username: String,
        species: String,
        weight: Double,
        length: Double,
        bait: String,
        notes: String,
        date: String,
        time: String,
        locationName: String,
        latitude: Double?,
        longitude: Double?,
        onResult: (Boolean, String) -> Unit
    ) {

        viewModelScope.launch {

            val newCatch = Catch(
                username = username,
                species = species,
                weight = weight,
                length = length,
                bait = bait,
                notes = notes,
                date = date,
                time = time,
                locationName = locationName,
                latitude = latitude,
                longitude = longitude
            )

            catchDao.insertCatch(newCatch)

            loadCatches(username)

            onResult(true, "Catch saved successfully.")
        }
    }

    fun loadCatches(username: String) {

        viewModelScope.launch {

            _catches.value = catchDao.getCatchesByUsername(username)
        }
    }
}