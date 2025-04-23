package com.squirtles.feature.userinfo

import androidx.compose.ui.unit.dp
import com.squirtles.core.model.User

internal object UserInfoConstants {
    const val USERNAME_PATTERN = "^[ㄱ-ㅎ|ㅏ-ㅣ가-힣a-zA-Z0-9]+$"
    val DEFAULT_USER = User("", "", "", null, listOf())

    // UI
    val MENU_PADDING_HORIZONTAL = 24.dp
    val MENU_PADDING_VERTICAL = 8.dp
}
