package com.squirtles.domain.preference.usecase

import com.squirtles.domain.preference.PreferenceRepository
import javax.inject.Inject

class LoadPlayerPreferenceUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    operator fun invoke() = preferenceRepository.loadPlayerPreference()
}
