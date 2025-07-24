package com.squirtles.feature.permission

import androidx.compose.ui.graphics.vector.ImageVector
import com.squirtles.core.common.ui.MusicRoadPermissions

internal data class PermissionData(
    val permission: String,
    val imageVector: ImageVector,
    val contentDescription: String,
    val permissionTitle: String,
    val permissionDescription: String,
) {
    val isOptional: Boolean
        get() = MusicRoadPermissions.OPTIONAL_PERMISSIONS.contains(permission)
}
