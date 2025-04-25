package com.squirtles.core.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squirtles.domain.preference.PlayerPreference
import com.squirtles.domain.preference.usecase.ReadPlayerPreferenceUseCase
import com.squirtles.domain.preference.usecase.SavePlayerPreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val savePlayerPreferenceUseCase: SavePlayerPreferenceUseCase,
    private val readPlayerPreferenceUseCase: ReadPlayerPreferenceUseCase
) : ViewModel() {

    val playerPreference = readPlayerPreferenceUseCase.invoke()

    fun savePlayerPreference(playerPreference: PlayerPreference) {
        viewModelScope.launch {
            savePlayerPreferenceUseCase.invoke(playerPreference)
                .onFailure {
                    // TODO: Exception handling
                }
        }
    }
}
