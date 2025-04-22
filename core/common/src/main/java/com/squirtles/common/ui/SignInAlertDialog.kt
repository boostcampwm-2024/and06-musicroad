package com.squirtles.common.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squirtles.common.R
import com.squirtles.common.ui.theme.Black
import com.squirtles.common.ui.theme.DarkGray
import com.squirtles.common.ui.theme.MusicRoadTheme
import com.squirtles.common.ui.theme.SignInButtonDarkBackground
import com.squirtles.common.ui.theme.SignInButtonDarkStroke
import com.squirtles.common.ui.theme.SignInButtonLightStroke
import com.squirtles.common.ui.theme.White


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInAlertDialog(
    onDismissRequest: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    description: String
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )

                VerticalSpacer(height = 40)

                GoogleSignInButton(onClick = onGoogleSignInClick)

                VerticalSpacer(height = 20)

                Text(
                    text = stringResource(R.string.sign_in_dialog_dismiss),
                    modifier = Modifier.clickable(onClick = onDismissRequest),
                    color = DarkGray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = if (isSystemInDarkTheme()) SignInButtonDarkBackground else White),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) SignInButtonDarkStroke else SignInButtonLightStroke),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.img_google_logo),
                contentDescription = stringResource(id = R.string.profile_google_icon),
                modifier = Modifier.size(20.dp)
            )
            HorizontalSpacer(10)
            Text(
                stringResource(
                    id = R.string.profile_sign_in_google
                ),
                color = if (isSystemInDarkTheme()) White else Black
            )
        }
    }
}

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun LoginAlertDialogPreview() {
    MusicRoadTheme {
        SignInAlertDialog({}, {}, stringResource(id = R.string.sign_in_dialog_title_default))
    }
}


@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun PreviewGoogleSignInButton() {
    MusicRoadTheme {
        GoogleSignInButton({})
    }
}
