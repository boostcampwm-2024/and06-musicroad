package com.squirtles.feature.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.core.common.ui.VerticalSpacer
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.DarkGray
import com.squirtles.core.common.ui.theme.MusicRoadTheme
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.common.ui.theme.White
import com.squirtles.feature.map.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    onCloseClick: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = {},
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = White
        ) {
            Column(
                modifier = Modifier.padding(top = 48.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = Primary
                )

                VerticalSpacer(16)

                Text(
                    text = stringResource(R.string.loading_current_location_dialog_text),
                    color = Black,
                    style = MaterialTheme.typography.bodyLarge
                )

                VerticalSpacer(24)

                TextButton(
                    onClick = { onCloseClick() },
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = Color.Transparent,
                        contentColor = DarkGray
                    )
                ) {
                    Text(
                        text = stringResource(R.string.close_loading_current_location_dialog)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LoadingDialogPreview() {
    MusicRoadTheme {
        LoadingDialog(
            onCloseClick = {}
        )
    }
}
