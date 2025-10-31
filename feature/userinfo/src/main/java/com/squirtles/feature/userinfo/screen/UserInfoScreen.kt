package com.squirtles.feature.userinfo.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.util.Log
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebSettings.LOAD_DEFAULT
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.outlined.AllOut
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.SwitchAccount
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.squirtles.core.account.AccountViewModel
import com.squirtles.core.account.GoogleId
import com.squirtles.core.common.ui.Constants.COLOR_STOPS
import com.squirtles.core.common.ui.DefaultTopAppBar
import com.squirtles.core.common.ui.DialogTextButton
import com.squirtles.core.common.ui.HorizontalSpacer
import com.squirtles.core.common.ui.MessageAlertDialog
import com.squirtles.core.common.ui.VerticalSpacer
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.common.ui.theme.White
import com.squirtles.core.model.User
import com.squirtles.feature.userinfo.R
import com.squirtles.feature.userinfo.UserInfoViewModel
import com.squirtles.feature.userinfo.components.MenuItem
import com.squirtles.feature.userinfo.components.UserInfoMenus
import kotlinx.coroutines.launch

@Composable
fun UserInfoScreen(
    uid: String,
    onBackClick: () -> Unit,
    onBackToMapClick: () -> Unit,
    onFavoritePicksClick: (String) -> Unit,
    onMyPicksClick: (String) -> Unit,
    onEditProfileClick: (String, String?) -> Unit,
    onEditNotificationClick: () -> Unit,
    onEditPlayerClick: () -> Unit,
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val user by userInfoViewModel.profileUser.collectAsStateWithLifecycle()

    var showLogOutDialog by remember { mutableStateOf(false) }
    var showLoadingIndicator by rememberSaveable { mutableStateOf(false) }

    val onSignOutClick: () -> Unit = {
        GoogleId(context).signOut()
        accountViewModel.signOut()
    }

    BackHandler(enabled = showLoadingIndicator) { }

    LaunchedEffect(Unit) {
        uid.let {
            userInfoViewModel.getUserById(uid)
        }

        launch {
            accountViewModel.signOutSuccess
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isSuccess ->
                    if (isSuccess) {
                        showLoadingIndicator = false
                        onBackToMapClick()
                    }
                }
        }
    }

    UserInfoScreenContent(
        user = user,
        isOwner = uid == userInfoViewModel.currentUid,
        showLogOutDialog = showLogOutDialog,
        showLoadingIndicator = showLoadingIndicator,
        onBackClick = onBackClick,
        onBackToMapClick = onBackToMapClick,
        onFavoritePicksClick = { onFavoritePicksClick(user.uid) },
        onMyPicksClick = { onMyPicksClick(user.uid) },
        onEditProfileClick = { onEditProfileClick(user.userName, user.userProfileImage) },
        onEditNotificationClick = onEditNotificationClick,
        onEditPlayerClick = onEditPlayerClick,
        onLogOutMenuClick = { showLogOutDialog = true },
        onDismissLogOutDialog = { showLogOutDialog = false },
        onConfirmLogOutDialog = {
            showLogOutDialog = false
            showLoadingIndicator = true
            onSignOutClick()
        }
    )
}

@Composable
fun UserInfoScreenContent(
    user: User,
    isOwner: Boolean,
    showLogOutDialog: Boolean,
    showLoadingIndicator: Boolean,
    onBackClick: () -> Unit,
    onBackToMapClick: () -> Unit,
    onFavoritePicksClick: () -> Unit,
    onMyPicksClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onEditNotificationClick: () -> Unit,
    onEditPlayerClick: () -> Unit,
    onLogOutMenuClick: () -> Unit,
    onDismissLogOutDialog: () -> Unit,
    onConfirmLogOutDialog: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // WebPages
    val askUrl = stringResource(R.string.ask_page)
    val termsUrl = stringResource(R.string.terms_page)
    val policyUrl = stringResource(R.string.privacy_page)

    val startBrowser: (String) -> Unit = { url ->
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = user.userName,
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
                    .verticalScroll(scrollState)
                    .padding(bottom = 96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                VerticalSpacer(16)

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.userProfileImage)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(R.string.user_info_profile_image),
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.img_user_default_profile),
                    error = painterResource(R.drawable.img_user_default_profile),
                    contentScale = ContentScale.Crop,
                )

                VerticalSpacer(30)

                UserInfoMenus(
                    title = stringResource(R.string.user_info_pick_category_title),
                    menus = listOf(
                        MenuItem(
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = stringResource(R.string.user_info_favorite_menu_icon_description),
                            menuTitle = stringResource(R.string.user_info_favorite_menu_title),
                            onMenuClick = onFavoritePicksClick
                        ),
                        MenuItem(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = stringResource(R.string.user_info_created_by_self_menu_icon_description),
                            menuTitle = stringResource(R.string.user_info_created_by_self_menu_title),
                            onMenuClick = onMyPicksClick
                        )
                    )
                )

                if (isOwner) {
                    UserInfoMenus(
                        title = stringResource(R.string.user_info_setting_category_title),
                        menus = listOf(
                            MenuItem(
                                imageVector = Icons.Outlined.SwitchAccount,
                                contentDescription = stringResource(R.string.user_info_setting_profile_menu_icon_description),
                                menuTitle = stringResource(R.string.user_info_setting_profile_menu_title),
                                onMenuClick = onEditProfileClick
                            ),
                            MenuItem(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = stringResource(R.string.user_info_setting_notification_menu_icon_description),
                                menuTitle = stringResource(R.string.user_info_setting_notification_menu_title),
                                onMenuClick = onEditNotificationClick
                            ),
                            MenuItem(
                                imageVector = Icons.Outlined.SmartDisplay,
                                contentDescription = stringResource(R.string.user_info_setting_sound_effect_menu_icon_description),
                                menuTitle = stringResource(R.string.user_info_setting_sound_effect_menu_title),
                                onMenuClick = onEditPlayerClick
                            ),
                            MenuItem(
                                imageVector = Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = stringResource(R.string.user_info_setting_sign_out_menu_icon_description),
                                menuTitle = stringResource(R.string.user_info_setting_sign_out_menu_title),
                                onMenuClick = onLogOutMenuClick
                            )
                        )
                    )

                    UserInfoMenus(
                        title = stringResource(R.string.user_info_support_category_title),
                        menus = listOf(
                            MenuItem(
                                imageVector = Icons.Outlined.EditNote,
                                contentDescription = stringResource(R.string.user_info_support_ask_icon_description),
                                menuTitle = stringResource(R.string.user_info_support_ask_title),
                                onMenuClick = {
                                    startBrowser(askUrl)
                                }
                            ),
                            MenuItem(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = stringResource(R.string.user_info_support_terms_icon_description),
                                menuTitle = stringResource(R.string.user_info_support_terms_title),
                                onMenuClick = {
                                    startBrowser(termsUrl)
                                }
                            ),
                            MenuItem(
                                imageVector = Icons.Default.Policy,
                                contentDescription = stringResource(R.string.user_info_support_policy_icon_description),
                                menuTitle = stringResource(R.string.user_info_support_policy_title),
                                onMenuClick = {
                                    startBrowser(policyUrl)
                                }
                            )
                        )
                    )
                }
            }

            ExtendedFloatingActionButton(
                onClick = onBackToMapClick,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 48.dp)
                    .align(Alignment.BottomCenter),
                shape = CircleShape,
                containerColor = Primary,
                contentColor = White,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = stringResource(R.string.user_info_icon_map_description),
                    tint = White
                )
                HorizontalSpacer(8)
                Text(
                    text = stringResource(R.string.user_info_back_to_map_button_text),
                    color = White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (showLogOutDialog) {
                MessageAlertDialog(
                    onDismissRequest = onDismissLogOutDialog,
                    title = stringResource(R.string.sign_out_dialog_title),
                    body = "",
                    showBody = false
                ) {
                    DialogTextButton(
                        onClick = onDismissLogOutDialog,
                        text = stringResource(R.string.sign_out_dialog_dismiss)
                    )

                    HorizontalSpacer(8)

                    DialogTextButton(
                        onClick = onConfirmLogOutDialog,
                        text = stringResource(R.string.sign_out_dialog_confirm),
                        textColor = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (showLoadingIndicator) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Black.copy(alpha = 0.5F))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserInfoScreenPreview() {
    val dummyUser = User(
        userName = "홍길동",
        userProfileImage = null,
        uid = "fqfqwegag",
        email = "email@email.com",
        myPicks = emptyList(),
    )

    UserInfoScreenContent(
        user = dummyUser,
        isOwner = true,
        showLogOutDialog = false,
        showLoadingIndicator = false,
        onBackClick = {},
        onBackToMapClick = {},
        onFavoritePicksClick = {},
        onMyPicksClick = {},
        onEditProfileClick = {},
        onEditNotificationClick = {},
        onEditPlayerClick = {},
        onLogOutMenuClick = {},
        onDismissLogOutDialog = {},
        onConfirmLogOutDialog = {}
    )
}
