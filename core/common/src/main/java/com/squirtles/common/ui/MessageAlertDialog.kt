package com.squirtles.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.common.R
import com.squirtles.common.ui.theme.Black
import com.squirtles.common.ui.theme.MusicRoadTheme
import com.squirtles.common.ui.theme.Primary
import com.squirtles.common.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageAlertDialog(
    title: String,
    body: String,
    onDismissRequest: () -> Unit,
    showBody: Boolean = true,
    buttons: @Composable RowScope.() -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = White
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    color = Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (showBody) {
                    VerticalSpacer(8)

                    Text(
                        text = body,
                        color = Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                VerticalSpacer(24)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = buttons
                )
            }
        }
    }
}

@Composable
fun DialogTextButton(
    onClick: () -> Unit,
    text: String,
    textColor: Color = Black,
    buttonColor: Color = Color.Transparent,
    fontWeight: FontWeight? = null,
) {
    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = buttonColor,
            contentColor = textColor
        )
    ) {
        Text(
            text = text,
            fontWeight = fontWeight,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeletePickDialogPreview() {
    MusicRoadTheme {
        MessageAlertDialog(
            onDismissRequest = {},
            title = stringResource(R.string.delete_pick_dialog_title),
            body = stringResource(R.string.delete_pick_dialog_body),
            buttons = {
                DialogTextButton(
                    onClick = {},
                    text = "취소"
                )

                HorizontalSpacer(8)

                DialogTextButton(
                    onClick = {},
                    text = "삭제하기",
                    textColor = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        )
    }
}
