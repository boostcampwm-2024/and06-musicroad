package com.squirtles.feature.map

import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.naver.maps.map.clustering.Clusterer
import com.squirtles.core.account.AccountViewModel
import com.squirtles.core.account.GoogleId
import com.squirtles.core.common.ui.DoubleBackPressToExit
import com.squirtles.core.common.ui.MusicRoadPermissions.checkLocationPermission
import com.squirtles.core.common.ui.SignInAlertDialog
import com.squirtles.core.common.ui.VerticalSpacer
import com.squirtles.core.common.ui.theme.Black
import com.squirtles.core.common.ui.theme.MusicRoadTheme
import com.squirtles.core.model.LocationPoint
import com.squirtles.core.model.Pick
import com.squirtles.core.model.PlayerState
import com.squirtles.core.musicplayer.PlayerServiceViewModel
import com.squirtles.feature.map.components.ClusterBottomSheet
import com.squirtles.feature.map.components.InfoWindow
import com.squirtles.feature.map.components.LoadingDialog
import com.squirtles.feature.map.components.MapBottomNavBar
import com.squirtles.feature.map.components.PickNotificationBanner
import com.squirtles.feature.map.marker.MarkerKey
import com.squirtles.feature.map.marker.buildClusterer
import kotlinx.coroutines.launch

private data class MapClickActions(
    val onFavoriteClick: (String) -> Unit,
    val onCenterClick: () -> Unit,
    val onUserInfoClick: (String) -> Unit,
    val onPickSummaryClick: (String) -> Unit,
) {
    companion object {
        val preview = MapClickActions(
            onFavoriteClick = {},
            onCenterClick = {},
            onUserInfoClick = {},
            onPickSummaryClick = {},
        )
    }
}

private data class SignInActions(
    val setShowSignInDialog: (Boolean) -> Unit,
    val setSignInDialogDescription: (String) -> Unit,
    val setSignInSuccess: ((String) -> Unit) -> Unit,
) {
    companion object {
        val preview = SignInActions(
            setShowSignInDialog = {},
            setSignInDialogDescription = {},
            setSignInSuccess = {}
        )
    }
}

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
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val nearPicks by mapViewModel.nearPicks.collectAsStateWithLifecycle()
    val lastLocation by mapViewModel.lastLocation.collectAsStateWithLifecycle()
    val clickedMarkerState by mapViewModel.clickedMarkerState.collectAsStateWithLifecycle()
    val centerPoint by mapViewModel.centerPoint.collectAsStateWithLifecycle()
    val playerState by playerServiceViewModel.playerState.collectAsStateWithLifecycle()

    var showLocationLoading by rememberSaveable { mutableStateOf(true) }

    // Sign In Dialog
    var showSignInDialog by remember { mutableStateOf(false) }
    var signInDialogDescription by remember { mutableStateOf("") }
    var onSignInSuccess by remember { mutableStateOf<(String) -> Unit>({}) }
    var showLoadingIndicator by rememberSaveable { mutableStateOf(false) }

    // permission
    val hasPermission by remember { mutableStateOf(checkLocationPermission(context)) }

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
        if (hasPermission) {
            showLocationLoading = lastLocation == null
        } else {
            showLocationLoading = false
        }
    }

    MapScreenContent(
        lastLocation = lastLocation,
        picks = mapViewModel.picks,
        nearPicks = nearPicks,
        playerState = playerState,
        clickedMarkerState = clickedMarkerState,
        naverMapActions = NaverMapActions(
            fetchPicksInBounds = mapViewModel::fetchPicksInBounds,
            resetClickedMarkerState = mapViewModel::resetClickedMarkerState,
            requestPickNotificationArea = mapViewModel::requestPickNotificationArea,
            updateCurLocation = mapViewModel::updateCurLocation,
            updateCenterLocation = mapViewModel::updateCenterLocation,
            setLastCameraPosition = mapViewModel::setLastCameraPosition,
            getLastCameraPosition = { mapViewModel.lastCameraPosition },
        ),
        mapClickActions = MapClickActions(
            onFavoriteClick = onFavoriteClick,
            onCenterClick = {
                mapViewModel.saveCurLocationForced()
                onCenterClick()
            },
            onUserInfoClick = onUserInfoClick,
            onPickSummaryClick = onPickSummaryClick,
        ),
        signInActions = SignInActions(
            setShowSignInDialog = { showSignInDialog = it },
            setSignInDialogDescription = { signInDialogDescription = it },
            setSignInSuccess = { onSignInSuccess = it }
        ),
        shuffleNextPick = playerServiceViewModel::shuffleNext,
        calculateDistance = mapViewModel::calculateDistance,
        getUid = mapViewModel::getUid,
        centerPoint = centerPoint,
        getClusterer = { buildClusterer(context, mapViewModel) },
        hasPermission = { hasPermission }
    )

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

@Composable
private fun MapScreenContent(
    centerPoint: LocationPoint?,
    lastLocation: LocationPoint?,
    picks: Map<String, Pick>,
    nearPicks: List<Pick>,
    playerState: PlayerState,
    clickedMarkerState: MarkerState,
    naverMapActions: NaverMapActions,
    mapClickActions: MapClickActions,
    signInActions: SignInActions,
    shuffleNextPick: (Pick) -> Unit,
    calculateDistance: (Double, Double) -> Double,
    getUid: () -> String?,
    getClusterer: () -> Clusterer<MarkerKey>?,
    hasPermission: () -> Boolean,
) {
    val context: Context = LocalContext.current

    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NaverMap(
                naverMapActions = naverMapActions,
                centerPoint = centerPoint,
                lastLocation = lastLocation,
                hasPermission = hasPermission,
                getClusterer = getClusterer
            )

            if (nearPicks.isNotEmpty()) {
                PickNotificationBanner(
                    nearPicks = nearPicks,
                    isPlaying = playerState.isPlaying,
                    onClick = {
                        shuffleNextPick(
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
                val lastCameraPosition = naverMapActions.getLastCameraPosition()
                if (lastCameraPosition != null &&
                    clickedMarkerState.prevClickedMarker?.position == lastCameraPosition.target
                ) {
                    naverMapActions.resetClickedMarkerState(context)
                } else {
                    clickedMarkerState.prevClickedMarker?.let {
                        if (clickedMarkerState.curPickId != null) { // 단말 마커 클릭 시
                            showBottomSheet = false
                            picks[clickedMarkerState.curPickId]?.let { pick ->
                                InfoWindow(
                                    pick = pick,
                                    uid = getUid(),
                                    navigateToPick = { pickId ->
                                        mapClickActions.onPickSummaryClick(pickId)
                                    },
                                    calculateDistance = { lat, lng ->
                                        calculateDistance(lat, lng).let { distance ->
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
                        getUid()?.let { uid ->
                            mapClickActions.onFavoriteClick(uid)
                        } ?: run {
                            signInActions.setSignInDialogDescription(
                                getString(context, R.string.sign_in_dialog_title_favorite_picks)
                            )
                            signInActions.setShowSignInDialog(true)
                            signInActions.setSignInSuccess(mapClickActions.onFavoriteClick)
                        }
                    },
                    onCenterClick = {
                        getUid()?.let {
                            mapClickActions.onCenterClick()
                        } ?: run {
                            signInActions.setSignInDialogDescription(
                                getString(context, R.string.sign_in_dialog_title_add_pick)
                            )
                            signInActions.setShowSignInDialog(true)
                            signInActions.setSignInSuccess {
                                mapClickActions.onCenterClick()
                            }
                        }
                    },
                    onUserInfoClick = {
                        getUid()?.let { uid ->
                            mapClickActions.onUserInfoClick(uid)
                        } ?: run {
                            signInActions.setSignInDialogDescription(
                                getString(context, R.string.sign_in_dialog)
                            )
                            signInActions.setShowSignInDialog(true)
                            signInActions.setSignInSuccess(mapClickActions.onUserInfoClick)
                        }
                    },
                )
            }

            if (showBottomSheet) {
                ClusterBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                        naverMapActions.resetClickedMarkerState(context)
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(WindowInsets.statusBars.asPaddingValues()),
                    clusterPickList = clickedMarkerState.clusterPickList,
                    uid = getUid(),
                    calculateDistance = { lat, lng ->
                        calculateDistance(lat, lng).let { distance ->
                            when {
                                distance >= 1000.0 -> "%.1fkm".format(distance / 1000.0)
                                distance >= 0 -> "%.0fm".format(distance)
                                else -> ""
                            }
                        }

                    },
                    onClickItem = { pickId ->
                        mapClickActions.onPickSummaryClick(pickId)
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun MapScreenPreview() {
    MusicRoadTheme {
        MapScreenContent(
            mapClickActions = MapClickActions.preview,
            signInActions = SignInActions.preview,
            naverMapActions = NaverMapActions.preview,
            lastLocation = null,
            picks = emptyMap(),
            nearPicks = emptyList(),
            playerState = PlayerState(),
            clickedMarkerState = MarkerState(),
            shuffleNextPick = { },
            calculateDistance = { _, _ -> 0.0 },
            getUid = { "" },
            centerPoint = null,
            getClusterer = { null },
            hasPermission = { true }
        )
    }
}
