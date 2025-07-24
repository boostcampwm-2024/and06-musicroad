package com.squirtles.feature.permission

import android.Manifest
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation

internal object PermissionDataFactory {
    fun from(permission: String, context: Context): PermissionData? {
        val resources = context.resources

        return when (permission) {
            Manifest.permission.RECORD_AUDIO -> PermissionData(
                permission = permission,
                imageVector = Icons.Default.Mic,
                contentDescription = resources.getString(R.string.permission_mic_content_desc),
                permissionTitle = resources.getString(R.string.permission_mic),
                permissionDescription = resources.getString(R.string.permission_mic_desc)
            )

            Manifest.permission.ACCESS_FINE_LOCATION -> null

            Manifest.permission.ACCESS_COARSE_LOCATION -> PermissionData(
                permission = permission,
                imageVector = Icons.Default.MyLocation,
                contentDescription = resources.getString(R.string.permission_location_content_desc),
                permissionTitle = resources.getString(R.string.permission_location),
                permissionDescription = resources.getString(R.string.permission_location_desc)
            )

            else -> throw IllegalArgumentException("Unsupported permission: $permission")
        }
    }
}
