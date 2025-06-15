package com.squirtles.feature.permission

import androidx.compose.ui.graphics.vector.ImageVector

internal data class PermissionData(
    val imageVector: ImageVector,
    val contentDescription: String,
    val permissionTitle: String,
    val permissionDescription: String,
    val isOptional: Boolean = false,
)
