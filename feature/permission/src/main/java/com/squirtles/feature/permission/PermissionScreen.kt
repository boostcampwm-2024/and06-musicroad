package com.squirtles.feature.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.squirtles.core.common.ui.DoubleBackPressToExit
import com.squirtles.core.common.ui.MusicRoadPermissions.ALL_PERMISSIONS
import com.squirtles.core.common.ui.MusicRoadPermissions.CORE_PERMISSIONS
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.DarkGray
import com.squirtles.core.common.ui.theme.Primary80
import com.squirtles.core.common.ui.theme.White


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionScreen(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
) {
    val context = LocalContext.current
    val permissionState = rememberMultiplePermissionsState(ALL_PERMISSIONS)

    var showPermissionBar by remember { mutableStateOf(false) }
    var hasRequestedPermission by remember { mutableStateOf(false) }

    DoubleBackPressToExit(onBackClick = onBackClick)

    Scaffold(
        bottomBar = {
            // 다음 버튼
            Box(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(color = Primary80)
                    .clickable {
                        // 필수 권한 중 허용되지 않은 권한
                        val deniedCorePermissions = permissionState.permissions.filter { permission ->
                            permission.permission in CORE_PERMISSIONS && !permission.status.isGranted
                        }

                        // 두번 이상 요청하여 더 이상 권한 요청 할 수 없는 권한 유무
                        val hasBlockedPermissions = hasRequestedPermission && deniedCorePermissions.any {
                            !it.status.shouldShowRationale
                        }

                        when {
                            deniedCorePermissions.isEmpty() -> {
                                onNextClick()
                            }

                            hasBlockedPermissions -> {
                                showPermissionBar = true
                            }

                            else -> {
                                hasRequestedPermission = true
                                permissionState.launchMultiplePermissionRequest()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.next),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Black
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .padding(50.dp)
                        .padding(top = 50.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = stringResource(R.string.permission_screen_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = Black
                    )
                    Text(
                        text = stringResource(R.string.permission_screen_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkGray
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                    )

                    PermissionMenus(
                        items = listOf(
                            PermissionData(
                                imageVector = Icons.Default.Mic,
                                contentDescription = stringResource(R.string.permission_mic_content_desc),
                                permissionTitle = stringResource(R.string.permission_mic),
                                permissionDescription = stringResource(R.string.permission_mic_desc)
                            ),
                            PermissionData(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = stringResource(R.string.permission_location_content_desc),
                                permissionTitle = stringResource(R.string.permission_location),
                                permissionDescription = stringResource(R.string.permission_location_desc),
                            )
                        )
                    )
                }
            }

            if (showPermissionBar) {
                PermissionBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .zIndex(1f), // 다른 요소 위로 띄우기
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                        showPermissionBar = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PermissionMenus(
    items: List<PermissionData>,
    modifier: Modifier = Modifier
) {
    val essentialString = stringResource(R.string.essential)
    val optionalString = stringResource(R.string.optional)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        for (item in items) {
            PermissionItem(
                imageVector = item.imageVector,
                contentDescription = item.contentDescription,
                permissionTitle = item.permissionTitle + " " + if (item.isOptional) optionalString else essentialString,
                permissionDescription = item.permissionDescription
            )
        }
    }
}

@Composable
private fun PermissionItem(
    imageVector: ImageVector,
    contentDescription: String,
    permissionTitle: String,
    permissionDescription: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .background(
                    color = Primary80,
                    shape = CircleShape,
                )
                .padding(10.dp),
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Black
        )

        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = permissionTitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Black
            )
            Text(
                text = permissionDescription,
                style = MaterialTheme.typography.bodySmall,
                color = DarkGray
            )
        }
    }
}

@Preview
@Composable
private fun PermissionScreenPreview() {
    PermissionScreen(
        {}, {}
    )
}
