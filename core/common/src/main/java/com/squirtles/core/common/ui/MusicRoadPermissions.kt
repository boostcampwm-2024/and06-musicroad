package com.squirtles.core.common.ui

import android.Manifest
import android.content.Context
import androidx.core.content.PermissionChecker

object MusicRoadPermissions {
    // 선택적 권한
    val OPTIONAL_PERMISSIONS = listOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    // 필수 권한
    val CORE_PERMISSIONS = listOf<String>(
        Manifest.permission.RECORD_AUDIO,
    )

    val ALL_PERMISSIONS = CORE_PERMISSIONS + OPTIONAL_PERMISSIONS

    fun checkLocationPermission(context: Context): Boolean {
        return OPTIONAL_PERMISSIONS.all {
            PermissionChecker.checkSelfPermission(context, it) == PermissionChecker.PERMISSION_GRANTED
        }
    }
}
