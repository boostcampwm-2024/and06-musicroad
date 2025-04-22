package com.squirtles.musicroad.userinfo.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.wear.compose.material.Text
import com.squirtles.common.ui.Constants.COLOR_STOPS
import com.squirtles.common.ui.Constants.DEFAULT_PADDING
import com.squirtles.common.ui.DefaultTopAppBar
import com.squirtles.common.ui.theme.White
import com.squirtles.musicroad.R

@Composable
internal fun EditNotificationSettingScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(id = R.string.setting_notification_title),
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = COLOR_STOPS))
                .padding(innerPadding)
        ) {
            Text(
                text = stringResource(id = R.string.setting_notification_description),
                modifier = Modifier
                    .padding(horizontal = DEFAULT_PADDING)
                    .align(Alignment.Center),
                color = White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
