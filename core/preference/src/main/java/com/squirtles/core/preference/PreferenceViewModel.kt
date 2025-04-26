package com.squirtles.core.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.preference.PlayerPreference
import com.squirtles.domain.preference.usecase.LoadPlayerPreferenceUseCase
import com.squirtles.domain.preference.usecase.SavePlayerPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val savePlayerPreferenceUseCase: SavePlayerPreferenceUseCase,
    private val loadPlayerPreferenceUseCase: LoadPlayerPreferenceUseCase
) : ViewModel() {

    private val _playerPreference = MutableStateFlow<PlayerPreference?>(null)
    val playerPreference: StateFlow<PlayerPreference?> = _playerPreference

    init {
        loadPlayerPreference()
    }

    private fun loadPlayerPreference() {
        viewModelScope.launch {
            val pref = loadPlayerPreferenceUseCase()
            pref.collect {
                _playerPreference.emit(it)
            }
        }
    }

    fun savePlayerPreference(playerPreference: PlayerPreference) {
        viewModelScope.launch {
            savePlayerPreferenceUseCase(playerPreference)
                .onSuccess {
                    _playerPreference.emit(playerPreference)
                }
                .onFailure {
                    // TODO: Exception handling
                }
        }
    }
}
