package com.squirtles.feature.userinfo.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.squirtles.core.common.ui.Constants.COLOR_STOPS
import com.squirtles.core.common.ui.DefaultTopAppBar
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.common.ui.theme.White
import com.squirtles.feature.userinfo.R

@Composable
fun EditPlayerScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    val effectStringList = listOf(
        stringResource(R.string.sound_effect_none),
        stringResource(R.string.sound_effect_bar),
        stringResource(R.string.sound_effect_wave_stroke),
        stringResource(R.string.sound_effect_wave_fill)
    )
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(effectStringList[1]) }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = "플레이어 설정",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = COLOR_STOPS))
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .weight(1f),
                ) {
                    SoundEffectItem(
                        imageRes = R.drawable.soundeffectnone,
                        effectName = effectStringList[0],
                        selectedOption = selectedOption,
                        onClick = { onOptionSelected(effectStringList[0]) },
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )

                    SoundEffectItem(
                        imageRes = R.drawable.soundeffectbar,
                        effectName = effectStringList[1],
                        selectedOption = selectedOption,
                        onClick = { onOptionSelected(effectStringList[1]) },
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .weight(1f)
                ) {
                    SoundEffectItem(
                        imageRes = R.drawable.soundeffectstroke,
                        effectName = effectStringList[2],
                        selectedOption = selectedOption,
                        onClick = { onOptionSelected(effectStringList[2]) },
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        imagePadding = 15.dp
                    )

                    SoundEffectItem(
                        imageRes = R.drawable.soundeffectfill,
                        effectName = effectStringList[3],
                        selectedOption = selectedOption,
                        onClick = { onOptionSelected(effectStringList[3]) },
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                        imagePadding = 15.dp
                    )
                }
            }
        }
    }
}

@Composable
fun SoundEffectItem(
    @DrawableRes imageRes: Int,
    effectName: String,
    selectedOption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imagePadding: Dp = 0.dp
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(imagePadding)
        )
        SelectEffectButton(
            text = effectName,
            selectedOption = selectedOption,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SelectEffectButton(
    text: String,
    selectedOption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .selectable(
                selected = (text == selectedOption),
                onClick = onClick,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (text == selectedOption),
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = Primary
            )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = White,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditPlayerScreenPreview() {
    EditPlayerScreen(
        onBackClick = { }
    )
}
