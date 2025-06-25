package com.squirtles.core.common.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.squirtles.core.common.R

@Composable
fun DoubleBackPressToExit(enabled: Boolean = true, onBackClick: () -> Unit) {
    var backPressedTime by remember { mutableLongStateOf(0L) }
    val context = LocalContext.current
    val backExitString = stringResource(R.string.back_to_exit)

    BackHandler(enabled = enabled) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            onBackClick()
        } else {
            backPressedTime = currentTime
            Toast.makeText(context, backExitString, Toast.LENGTH_SHORT).show()
        }
    }
}
