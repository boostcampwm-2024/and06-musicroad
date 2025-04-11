package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

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

internal fun getOffset(
    centerX: Float,
    centerY: Float,
    angle: Double,
    radius: Float,
    extraLength: Float,
): Offset {
    return Offset(
        (centerX + (radius + extraLength) * cos(angle)).toFloat(),
        (centerY + (radius + extraLength) * sin(angle)).toFloat()
    )
}

