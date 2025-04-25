package com.squirtles.domain.preference.usecase

import com.squirtles.domain.preference.PlayerPreference
import com.squirtles.domain.preference.PreferenceRepository
import javax.inject.Inject

class SavePlayerPreferenceUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    suspend fun invoke(preference: PlayerPreference) = preferenceRepository.savePlayerPreference(preference)
}
