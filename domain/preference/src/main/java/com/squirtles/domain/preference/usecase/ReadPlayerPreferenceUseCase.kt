package com.squirtles.domain.preference.usecase

import com.squirtles.domain.preference.PreferenceRepository
import javax.inject.Inject

class ReadPlayerPreferenceUseCase @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) {
    fun invoke() = preferenceRepository.readPlayerPreference()
}
