package com.miller198.audiovisualizer

/**
 * Represents a frequency range (as a ratio of the full signal) and its corresponding scaling weight.
 *
 * @property rangeRatio A ratio-based range from 0.0 to 1.0 indicating the portion of the frequency spectrum.
 * @property weight The scaling factor to apply to values within this range.
 */
data class FrequencyScale(
    val rangeRatio: ClosedFloatingPointRange<Float>,
    val weight: Float
)
