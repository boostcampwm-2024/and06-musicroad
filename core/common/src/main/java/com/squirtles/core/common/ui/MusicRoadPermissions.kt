package com.squirtles.core.common.ui

import android.Manifest

object MusicRoadPermissions {
    // 선택적 권한
    val OPTIONAL_PERMISSIONS = listOf<String>()

    // 필수 권한
    val CORE_PERMISSIONS = listOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.RECORD_AUDIO,
    )

    val ALL_PERMISSIONS = OPTIONAL_PERMISSIONS + CORE_PERMISSIONS
}
