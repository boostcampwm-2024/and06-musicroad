package com.squirtles.musicroad.detail.components.music.visualizer.soundeffect

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

internal fun Path.catmullRomSpline(points: List<Offset>, tension: Float = 0.5f) {
    if (points.size < 4) return

    for (i in 1 until points.size - 2) {
        val p0 = points[i - 1]
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points[i + 2]

        for (t in 0..10) {
            val s = t / 10f
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

            if (i == 1 && t == 0) {
                moveTo(x, y)
            } else {
                lineTo(x, y)
            }
        }
    }
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

