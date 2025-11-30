package com.squirtles.feature.userinfo.screen

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import coil3.compose.AsyncImage
import com.squirtles.core.account.AccountViewModel
import com.squirtles.core.account.GoogleId
import com.squirtles.core.common.ui.Constants.COLOR_STOPS
import com.squirtles.core.common.ui.DialogTextButton
import com.squirtles.core.common.ui.HorizontalSpacer
import com.squirtles.core.common.ui.MessageAlertDialog
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.Gray
import com.squirtles.core.common.ui.theme.MusicRoadTheme
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.common.ui.theme.White
import com.squirtles.feature.userinfo.ProfileImageState
import com.squirtles.feature.userinfo.R
import com.squirtles.feature.userinfo.UserInfoConstants.USERNAME_PATTERN
import com.squirtles.feature.userinfo.UserInfoViewModel
import com.squirtles.feature.userinfo.UserNameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.regex.Pattern

@Composable
internal fun EditProfileScreen(
    currentUserName: String,
    currentUserProfileImage: String?,
    onBackToMapClick: () -> Unit,
    onBackClick: () -> Unit,
    userInfoViewModel: UserInfoViewModel = hiltViewModel(),
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val userName = rememberSaveable { mutableStateOf(currentUserName) }
    val nickNameErrorMessage = rememberSaveable { mutableStateOf("") }
    var selectedImage by rememberSaveable { mutableStateOf(currentUserProfileImage?.toUri()) }

    var showLoadingIndicator by rememberSaveable { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val onDeleteAccountClick: () -> Unit = {
        GoogleId(context).signOut()
        accountViewModel.deleteAccount()
    }

    BackHandler(enabled = showLoadingIndicator) { }

    LaunchedEffect(Unit) {
        launch {
            userInfoViewModel.updateState
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { updateState ->
                    focusManager.clearFocus()
                    delay(100)

                    when {
                        updateState.nameSuccess && updateState.imageSuccess -> {
                            Toast.makeText(
                                context,
                                context.getString(R.string.setting_profile_update_nickname_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            onBackClick()
                        }

                        else -> {
                            showLoadingIndicator = false
                            Toast.makeText(
                                context,
                                context.getString(R.string.setting_profile_update_nickname_failure),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
        }

        launch {
            accountViewModel.deleteAccountSuccess
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isSuccess ->
                    if (isSuccess) {
                        showLoadingIndicator = false
                        onBackToMapClick()
                    }
                }
        }
    }

    Scaffold(
        topBar = {
            EditProfileAppBar(
                confirmEnabled = nickNameErrorMessage.value.isEmpty() &&
                        (currentUserName != userName.value || currentUserProfileImage?.toUri() != selectedImage),
                onConfirmClick = {
                    showLoadingIndicator = true
                    userInfoViewModel.updateProfile(
                        userNameState = if (currentUserName == userName.value) {
                            UserNameState.Unchanged
                        } else {
                            UserNameState.New(userName.value)
                        },
                        profileImageState = if (currentUserProfileImage?.toUri() == selectedImage) {
                            ProfileImageState.Unchanged
                        } else if (selectedImage == null) {
                            ProfileImageState.Remove
                        } else {
                            ProfileImageState.New(selectedImage!!)
                        }
                    )
                },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 수정
                EditProfileContents(
                    userName = userName,
                    nickNameErrorMessage = nickNameErrorMessage,
                    profileImage = selectedImage.toString(),
                    onImageSelected = { uri -> selectedImage = uri }
                )

                Spacer(modifier = Modifier.weight(1f))

                // 회원 탈퇴
                Text(
                    text = stringResource(id = R.string.user_info_setting_delete_user_account),
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .clickable { showDeleteAccountDialog = true },
                    color = Gray,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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

    if (showDeleteAccountDialog) {
        MessageAlertDialog(
            onDismissRequest = {
                showDeleteAccountDialog = false
            },
            title = stringResource(R.string.delete_account_dialog_title),
            body = stringResource(R.string.delete_account_dialog_description),
        ) {
            DialogTextButton(
                onClick = {
                    showDeleteAccountDialog = false
                },
                text = stringResource(R.string.delete_account_dialog_dismiss)
            )

            HorizontalSpacer(8)

            DialogTextButton(
                onClick = {
                    showLoadingIndicator = true
                    showDeleteAccountDialog = false
                    onDeleteAccountClick()
                },
                text = stringResource(R.string.delete_account_dialog_confirm),
                textColor = Primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileAppBar(
    confirmEnabled: Boolean,
    onConfirmClick: () -> Unit,
    onBackClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.setting_profile_title),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.top_app_bar_back_description),
                    tint = White
                )
            }
        },
        actions = {
            IconButton(
                onClick = onConfirmClick,
                enabled = confirmEnabled
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.setting_profile_confirm_icon_description),
                    tint = if (confirmEnabled) White else Gray
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
            containerColor = Color.Transparent,
            titleContentColor = White
        )
    )
}

private fun validateUserName(userName: String, context: Context) = when {
    userName.length < 2 -> context.getString(R.string.setting_profile_nickname_message_length_fail_min)
    userName.length > 10 -> context.getString(R.string.setting_profile_nickname_message_length_fail_max)
    Pattern.matches(USERNAME_PATTERN, userName).not() -> context.getString(R.string.setting_profile_nickname_message_format_fail)
    else -> ""
}

@Composable
private fun EditProfileContents(
    userName: MutableState<String>,
    nickNameErrorMessage: MutableState<String>,
    profileImage: String?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .padding(vertical = 30.dp, horizontal = 30.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileImagePicker(
            currentImageUrl = profileImage,
            onImageSelected = onImageSelected,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 32.dp)
        )

        Text(
            text = stringResource(id = R.string.setting_profile_nickname),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = White,
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedTextField(
            value = userName.value,
            onValueChange = {
                nickNameErrorMessage.value = validateUserName(it, context)
                userName.value = it
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text(nickNameErrorMessage.value) },
            isError = nickNameErrorMessage.value.isNotEmpty(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = White,
                unfocusedTextColor = White,
                errorTextColor = White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = White,
                focusedBorderColor = Gray,
                unfocusedBorderColor = Gray,
            )
        )
    }
}

@Composable
fun ProfileImagePicker(
    currentImageUrl: String?,
    onImageSelected: (Uri?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showProfileMenu by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> onImageSelected(uri) }

    Box(
        modifier = modifier.size(180.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .clickable { showProfileMenu = true }
                .background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (currentImageUrl != null) {
                AsyncImage(
                    model = currentImageUrl,
                    contentDescription = stringResource(R.string.user_info_profile_image),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    placeholder = painterResource(R.drawable.img_user_default_profile),
                    error = painterResource(R.drawable.img_user_default_profile),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.img_user_default_profile),
                    contentDescription = stringResource(R.string.user_info_profile_image),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp)
                .size(50.dp)
                .clip(CircleShape)
                .clickable { showProfileMenu = true }
                .background(Color.White, CircleShape)
                .border(2.dp, Color.Gray.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = stringResource(R.string.setting_profile_image_description),
                modifier = Modifier.size(36.dp),
                tint = Color.Gray
            )
        }

        ProfileOptionsDropdown(
            expanded = showProfileMenu,
            onDismiss = { showProfileMenu = false },
            onGalleryClick = {
                galleryLauncher.launch("image/*")
                showProfileMenu = false
            },
            onDefaultClick = {
                onImageSelected(null)
                showProfileMenu = false
            }
        )
    }
}

@Composable
fun ProfileOptionsDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onGalleryClick: () -> Unit,
    onDefaultClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        offset = DpOffset(0.dp, (-20).dp),
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp),
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.setting_profile_image_option_album),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                onGalleryClick()
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        )

        DropdownMenuItem(
            text = {
                Text(
                    text = stringResource(R.string.setting_profile_image_option_default),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = {
                onDefaultClick()
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@Preview
@Composable
private fun EditProfileAppBarPreview() {
    EditProfileAppBar(false, {}, {})
}

@Preview
@Composable
private fun EditProfileContentPreview() {
    MusicRoadTheme {
        EditProfileContents(
            userName = remember { mutableStateOf("짱구") },
            nickNameErrorMessage = remember { mutableStateOf("") },
            profileImage = null,
            onImageSelected = {}
        )
    }
}
