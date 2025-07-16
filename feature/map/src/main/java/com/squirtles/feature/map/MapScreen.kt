package com.squirtles.feature.map

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.squirtles.core.account.AccountViewModel
import com.squirtles.core.account.GoogleId
import com.squirtles.core.common.ui.DoubleBackPressToExit
import com.squirtles.core.common.ui.MusicRoadPermissions.checkLocationPermission
import com.squirtles.core.common.ui.SignInAlertDialog
import com.squirtles.core.common.ui.VerticalSpacer
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.musicplayer.PlayerServiceViewModel
import com.squirtles.feature.map.components.ClusterBottomSheet
import com.squirtles.feature.map.components.InfoWindow
import com.squirtles.feature.map.components.LoadingDialog
import com.squirtles.feature.map.components.MapBottomNavBar
import com.squirtles.feature.map.components.PickNotificationBanner
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    mapViewModel: MapViewModel,
    playerServiceViewModel: PlayerServiceViewModel,
    onFavoriteClick: (String) -> Unit,
    onCenterClick: () -> Unit,
    onUserInfoClick: (String) -> Unit,
    onPickSummaryClick: (String) -> Unit,
    finishActivity: () -> Unit,
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val nearPicks by mapViewModel.nearPicks.collectAsStateWithLifecycle()
    val lastLocation by mapViewModel.lastLocation.collectAsStateWithLifecycle()

    val clickedMarkerState by mapViewModel.clickedMarkerState.collectAsStateWithLifecycle()
    val playerState by playerServiceViewModel.playerState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showLocationLoading by rememberSaveable { mutableStateOf(true) }

    // Sign In Dialog
    var showSignInDialog by remember { mutableStateOf(false) }
    var signInDialogDescription by remember { mutableStateOf("") }
    var onSignInSuccess by remember { mutableStateOf<(String) -> Unit>({}) }
    var showLoadingIndicator by rememberSaveable { mutableStateOf(false) }

    DoubleBackPressToExit(!showLoadingIndicator) {
        finishActivity()
    }

    LaunchedEffect(Unit) {
        playerServiceViewModel.readyPlayer()

        launch {
            accountViewModel.signInSuccess
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { isSuccess ->
                    if (isSuccess) {
                        showLoadingIndicator = false
                        mapViewModel.getUid()?.let { uid ->
                            onSignInSuccess(uid)
                        }
                    }
                }
        }

        launch {
            mapViewModel.fetchPicksErrorToast
                .flowWithLifecycle(lifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect {
                    Toast.makeText(
                        context,
                        context.getString(R.string.error_message_fetch_picks_in_bounds), Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    LaunchedEffect(lastLocation) {
        showLocationLoading = lastLocation == null
    }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NaverMap(
                mapViewModel = mapViewModel,
                lastLocation = lastLocation,
            )

            if (nearPicks.isNotEmpty()) {
                PickNotificationBanner(
                    nearPicks = nearPicks,
                    isPlaying = playerState.isPlaying,
                    onClick = {
                        playerServiceViewModel.shuffleNext(
                            if (nearPicks.size == 1) nearPicks.first()
                            else nearPicks.filter { it.id != playerState.id }.random()
                        )
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (mapViewModel.lastCameraPosition != null &&
                    clickedMarkerState.lastClickedMarker?.position == mapViewModel.lastCameraPosition?.target
                ) {
                    mapViewModel.resetClickedMarkerState(context)
                } else {
                    clickedMarkerState.lastClickedMarker?.let {
                        if (clickedMarkerState.curPickId != null) { // 단말 마커 클릭 시
                            showBottomSheet = false
                            mapViewModel.picks[clickedMarkerState.curPickId]?.let { pick ->
                                InfoWindow(
                                    pick = pick,
                                    uid = mapViewModel.getUid(),
                                    navigateToPick = { pickId ->
                                        onPickSummaryClick(pickId)
                                    },
                                    calculateDistance = { lat, lng ->
                                        mapViewModel.calculateDistance(lat, lng).let { distance ->
                                            when {
                                                distance >= 1000.0 -> "%.1fkm".format(distance / 1000.0)
                                                distance >= 0 -> "%.0fm".format(distance)
                                                else -> ""
                                            }
                                        }
                                    }
                                )
                            }
                        } else { // 클러스터 마커 클릭 시
                            showBottomSheet = true
                        }
                    }
                }

                VerticalSpacer(16)

                MapBottomNavBar(
                    modifier = Modifier.padding(bottom = 16.dp),
                    isActivated = checkLocationPermission(context),
                    onFavoriteClick = {
                        mapViewModel.getUid()?.let { uid ->
                            onFavoriteClick(uid)
                        } ?: run {
                            signInDialogDescription =
                                getString(context, R.string.sign_in_dialog_title_favorite_picks)
                            showSignInDialog = true
                            onSignInSuccess = onFavoriteClick
                        }
                    },
                    onCenterClick = {
                        mapViewModel.getUid()?.let {
                            onCenterClick()
                            mapViewModel.saveCurLocationForced()
                        } ?: run {
                            signInDialogDescription =
                                getString(context, R.string.sign_in_dialog_title_add_pick)
                            showSignInDialog = true
                            onSignInSuccess = {
                                onCenterClick()
                                mapViewModel.saveCurLocationForced()
                            }
                        }
                    },
                    onUserInfoClick = {
                        mapViewModel.getUid()?.let { uid ->
                            onUserInfoClick(uid)
                        } ?: run {
                            signInDialogDescription = getString(context, R.string.sign_in_dialog)
                            showSignInDialog = true
                            onSignInSuccess = onUserInfoClick
                        }
                    },
                )
            }

            if (showBottomSheet) {
                ClusterBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        mapViewModel.resetClickedMarkerState(context)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(WindowInsets.statusBars.asPaddingValues()),
                    clusterPickList = clickedMarkerState.clusterPickList,
                    uid = mapViewModel.getUid(),
                    calculateDistance = { lat, lng ->
                        mapViewModel.calculateDistance(lat, lng).let { distance ->
                            when {
                                distance >= 1000.0 -> "%.1fkm".format(distance / 1000.0)
                                distance >= 0 -> "%.0fm".format(distance)
                                else -> ""
                            }
                        }

                    },
                    onClickItem = { pickId ->
                        onPickSummaryClick(pickId)
                    }
                )
            }

            if (showLocationLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingDialog(
                        onCloseClick = {
                            finishActivity()
                        }
                    )
                }
            }
        }
    }

    if (showSignInDialog) {
        SignInAlertDialog(
            onDismissRequest = { showSignInDialog = false },
            onGoogleSignInClick = {
                showSignInDialog = false
                showLoadingIndicator = true
                GoogleId(context).signIn(
                    onSuccess = { uid, credential ->
                        accountViewModel.signIn(uid, credential)
                    },
                    onFailure = {
                        showLoadingIndicator = false
                    }
                )
            },
            description = signInDialogDescription
        )
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
