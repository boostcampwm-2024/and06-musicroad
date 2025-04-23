package com.squirtles.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.core.common.ui.HorizontalSpacer
import com.squirtles.core.common.ui.VerticalSpacer
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.DarkGray
import com.squirtles.core.common.ui.theme.MusicRoadTheme
import com.squirtles.core.common.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedPermissionDialog(
    showDialog: Boolean,
    onConfirmClick: () -> Unit,
) {
    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = {},
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.request_permissions_title),
                        color = Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    VerticalSpacer(8)

                    Text(
                        text = stringResource(R.string.request_permissions_body),
                        color = Black,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    VerticalSpacer(24)

                    TextButton(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = Color.Transparent,
                            contentColor = DarkGray
                        )
                    ) {
                        Text(stringResource(R.string.confirm_text))
                    }

                    HorizontalSpacer(8)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NeedPermissionDialogPreview() {
    MusicRoadTheme {
        NeedPermissionDialog(
            showDialog = true,
            onConfirmClick = {},
        )
    }
}
