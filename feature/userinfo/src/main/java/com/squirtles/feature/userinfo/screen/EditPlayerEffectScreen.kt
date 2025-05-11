package com.squirtles.feature.userinfo.screen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squirtles.core.common.ui.Constants.COLOR_STOPS
import com.squirtles.core.common.ui.DefaultTopAppBar
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.common.ui.theme.White
import com.squirtles.core.preference.PreferenceViewModel
import com.squirtles.domain.preference.PlayerPreference
import com.squirtles.feature.userinfo.R

data class PlayerEffect(
    val preference: PlayerPreference,
    @StringRes val name: Int,
    @DrawableRes val drawable: Int,
    val drawablePadding: Dp
)

enum class PlayerEffectType(val effect: PlayerEffect) {
    NONE(PlayerEffect(
        preference = PlayerPreference.NONE,
        name = R.string.sound_effect_none,
        drawable = R.drawable.soundeffectnone,
        drawablePadding = 0.dp
    )),
    BAR(PlayerEffect(
        preference = PlayerPreference.BAR,
        name = R.string.sound_effect_bar,
        drawable = R.drawable.soundeffectbar,
        drawablePadding = 0.dp
    )),
    FILL(PlayerEffect(
        preference = PlayerPreference.FILL,
        name = R.string.sound_effect_wave_fill,
        drawable = R.drawable.soundeffectfill,
        drawablePadding = 15.dp
    )),
    STROKE(PlayerEffect(
        preference = PlayerPreference.STROKE,
        name = R.string.sound_effect_wave_stroke,
        drawable = R.drawable.soundeffectstroke,
        drawablePadding = 15.dp
    )),
}

@Composable
fun EditPlayerScreen(
    onBackClick: () -> Unit,
    preferenceViewModel: PreferenceViewModel = hiltViewModel()
) {
    val currentEffect by preferenceViewModel.playerPreference.collectAsStateWithLifecycle(null)

    val savePreference: (PlayerPreference) -> Unit = {
        preferenceViewModel.savePlayerPreference(it)
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(id = R.string.edit_player_screen_top_app_bar_title),
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
            if (currentEffect != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 45.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        items(4) { index ->
                            val effectType = PlayerEffectType.entries[index]

                            SoundEffectItem(
                                imageRes = effectType.effect.drawable,
                                effectName = stringResource(effectType.effect.name),
                                effect = effectType.effect.preference,
                                currentEffect = currentEffect!!,
                                onClick = {
                                    savePreference(effectType.effect.preference)
                                },
                                imagePadding = effectType.effect.drawablePadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SoundEffectItem(
    @DrawableRes imageRes: Int,
    effect: PlayerPreference,
    effectName: String,
    currentEffect: PlayerPreference,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imagePadding: Dp = 0.dp
) {
    Column(
        modifier = modifier
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
            effect = effect,
            currentEffect = currentEffect,
            onClick = onClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SelectEffectButton(
    text: String,
    effect: PlayerPreference,
    currentEffect: PlayerPreference,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .selectable(
                selected = (effect == currentEffect),
                onClick = onClick,
                role = Role.RadioButton
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (effect == currentEffect),
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
