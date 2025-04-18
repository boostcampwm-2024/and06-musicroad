package com.miller198.audiovisualizer.soundeffect

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

private val DEFAULT_OFFSET_ANGLE = Math.toRadians(-90.0)

/**
 * Converts an audio data index to an angle (in radians) for circular visualizations.
 *
 * @param index The index of the audio data.
 * @param angleStep The step size between each index in degrees.
 * @param offset Optional angle offset in radians. Defaults to top.
 * @return The angle in radians as a [Float].
 */
internal fun getAudioDataAngle(
    index: Int,
    angleStep: Float,
    offset: Double = DEFAULT_OFFSET_ANGLE
): Float = (offset + Math.toRadians((index * angleStep).toDouble())).toFloat()

/**
 * Computes the screen position ([Offset]) for a point on a circle or arc.
 *
 * @param centerX The X coordinate of the center.
 * @param centerY The Y coordinate of the center.
 * @param angle The angle in radians.
 * @param innerRadius The base radius from the center.
 * @param extraLength Additional radial offset (e.g. for waveform height).
 * @return The [Offset] position on the canvas.
 */
internal fun getOffset(
    centerX: Float,
    centerY: Float,
    angle: Float,
    innerRadius: Float,
    extraLength: Float,
): Offset {
    return Offset(
        (centerX + (innerRadius + extraLength) * cos(angle)),
        (centerY + (innerRadius + extraLength) * sin(angle))
    )
}

/**
 * Draws a smooth, closed curve through a given list of [points] using the Catmull-Rom spline algorithm.
 *
 * This extension function generates a smooth path by interpolating between points using a Catmull-Rom spline.
 * The path automatically wraps around to create a closed loop, making it useful for circular or looping shapes.
 *
 * @param points The list of [Offset] points to interpolate.
 * @param steps The number of interpolation steps between each pair of points. Higher values create smoother curves.
 */
internal fun Path.catmullRomSpline(points: List<Offset>, steps: Int = 10) {
    if (points.size < 2) return

    val paddedPoints = listOf(points.last()) + points + listOf(points.first(), points[1])

    for (i in 0 until paddedPoints.size - 3) {
        val p0 = paddedPoints[i]
        val p1 = paddedPoints[i + 1]
        val p2 = paddedPoints[i + 2]
        val p3 = paddedPoints[i + 3]

        for (t in 0..steps) {
            val s = t / steps.toFloat()
            val s2 = s * s
            val s3 = s2 * s

            val x = 0.5f * ((2 * p1.x) +
                    (-p0.x + p2.x) * s +
                    (2 * p0.x - 5 * p1.x + 4 * p2.x - p3.x) * s2 +
                    (-p0.x + 3 * p1.x - 3 * p2.x + p3.x) * s3)

            val y = 0.5f * ((2 * p1.y) +
                    (-p0.y + p2.y) * s +
                    (2 * p0.y - 5 * p1.y + 4 * p2.y - p3.y) * s2 +
                    (-p0.y + 3 * p1.y - 3 * p2.y + p3.y) * s3)

            if (i == 0 && t == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
        }
    }
    close()
}
