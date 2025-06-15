package com.squirtles.feature.permission

import android.app.Activity
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.BottomAppBarDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.DarkGray
import com.squirtles.core.common.ui.theme.Primary80
import com.squirtles.core.common.ui.theme.White

@Composable
fun PermissionScreen(
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    DoubleBackPressToExit(onBackClick = onBackClick)

    Scaffold(
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
                                isOptional = true
                            )
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(color = Primary80)
                        .clickable {
                            onNextClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.next),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
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

@Composable
fun DoubleBackPressToExit(enabled: Boolean = true, onBackClick: () -> Unit) {
    var backPressedTime by remember { mutableStateOf(0L) }
    val context = LocalContext.current

    BackHandler(enabled = enabled) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 1500) {
            onBackClick()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, "한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
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
