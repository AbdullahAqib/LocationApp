package com.example.locationapp

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {

    private val _locationData = mutableStateOf<LocationData?>(null)
    val locationData : State<LocationData?> = _locationData

    fun updateLocationData(newLocation: LocationData) {
        _locationData.value = newLocation
    }
}