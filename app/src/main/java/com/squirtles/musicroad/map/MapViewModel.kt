package com.squirtles.musicroad.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _centerButtonClick = MutableSharedFlow<Boolean>()
    val centerButtonClick = _centerButtonClick.asSharedFlow()

    private val _curLocation = MutableStateFlow<Location?>(null)
    val curLocation = _curLocation.asStateFlow()

    fun createMarker() {
        viewModelScope.launch {
            Log.d("뷰모델2", "emit!")
            _centerButtonClick.emit(true)
        }
    }

    fun updateCurLocation(location: Location) {
        viewModelScope.launch {
            _curLocation.value = location
        }
    }
}