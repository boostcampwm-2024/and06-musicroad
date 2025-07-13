package com.squirtles.feature.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.location.Location
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.UiSettings
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.squirtles.core.common.ui.MusicRoadPermissions.checkLocationPermission
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.common.ui.theme.Purple15
import com.squirtles.core.model.LocationPoint
import com.squirtles.feature.map.marker.MarkerKey
import kotlinx.coroutines.launch

internal data class NaverMapActions(
    val fetchPicksInBounds: (LatLng, Clusterer<MarkerKey>?) -> Unit,
    val resetClickedMarkerState: (Context) -> Unit,
    val requestPickNotificationArea: (Double, Double, Double) -> Unit,
    val updateCurLocation: (Double, Double) -> Unit,
    val updateCenterLocation: (Double, Double) -> Unit,
    val setLastCameraPosition: (CameraPosition) -> Unit,
    val getLastCameraPosition: () -> CameraPosition?
) {
    companion object {
        val preview = NaverMapActions(
            requestPickNotificationArea = { _, _, _ -> },
            fetchPicksInBounds = { _, _ -> },
            setLastCameraPosition = { },
            resetClickedMarkerState = { },
            updateCenterLocation = { _, _ -> },
            updateCurLocation = { _, _ -> },
            getLastCameraPosition = { null }
        )
    }
}

@Composable
internal fun NaverMap(
    naverMapActions: NaverMapActions,
    centerPoint: LocationPoint?,
    lastLocation: LocationPoint?,
    hasPermission: () -> Boolean,
    getClusterer: () -> Clusterer<MarkerKey>?
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    // map
    val mapView = remember { MapView(context) }
    val naverMap = remember { mutableStateOf<NaverMap?>(null) }

    // overlays
    val locationOverlay = remember { mutableStateOf<LocationOverlay?>(null) }
    val circleOverlay = remember { CircleOverlay().apply { initCircleOverlay() } }

    // location sources
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationSource = remember { FusedLocationSource(context as Activity, LOCATION_PERMISSION_REQUEST_CODE) }
    var clusterer by remember { mutableStateOf<Clusterer<MarkerKey>?>(null) }

    LaunchedEffect(naverMap.value, lastLocation) {
        if (naverMap.value != null && !hasPermission()) {
            lastLocation?.let {
                naverMap.value?.initCameraPosition(naverMapActions.getLastCameraPosition(), lastLocation)
            }
        }

        lastLocation?.let {
            naverMapActions.requestPickNotificationArea(it.latitude, it.longitude, CIRCLE_RADIUS_METER)
        }
    }

    LaunchedEffect(centerPoint) {
        naverMap.value?.projection?.fromScreenLocation(PointF(0F, 0F))?.run {
            naverMapActions.fetchPicksInBounds(this, clusterer)
        }
    }

    DisposableEffect(Unit) {
        clusterer = getClusterer()

        onDispose {
            clusterer?.clear()

            naverMap.value?.let {
                naverMapActions.setLastCameraPosition(it.cameraPosition)
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val mapLifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }

        lifecycleOwner.lifecycle.addObserver(mapLifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(mapLifecycleObserver)
            mapView.onDestroy()
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                coroutineScope.launch {
                    mapView.getMapAsync { map ->
                        naverMap.value = map
                        map.run {
                            initMapSettings()
                            initLocationSource(hasPermission(), locationSource)
                            initDeviceLocation(
                                hasPermission = hasPermission(),
                                circleOverlay = circleOverlay,
                                fusedLocationClient = fusedLocationClient,
                                lastCameraPosition = naverMapActions.getLastCameraPosition()
                            )
                            initLocationOverlay(hasPermission()) { overlay ->
                                locationOverlay.value = overlay
                            }
                            moveCamera(CameraUpdate.zoomTo(INITIAL_CAMERA_ZOOM))
                            setLocationChangeListener(circleOverlay, naverMapActions.updateCurLocation)
                            setMapClickListener { naverMapActions.resetClickedMarkerState(context) }
                            setCameraIdleListener { centerLatLng ->
                                naverMapActions.updateCenterLocation(centerLatLng.latitude, centerLatLng.longitude)
                            }
                            clusterer?.map = this
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    if (isSystemInDarkTheme()) {
        naverMap.value?.isNightModeEnabled = true
    }
}

internal fun NaverMap.setCameraToMarker(
    clickedMarkerPosition: LatLng
) {
    val cameraUpdate = CameraUpdate
        .scrollTo(clickedMarkerPosition)
        .animate(CameraAnimation.Easing)
    moveCamera(cameraUpdate)
}

private fun NaverMap.initLocationSource(
    hasPermission: Boolean,
    currentLocationSource: FusedLocationSource,
) {
    if (hasPermission) {
        locationSource = currentLocationSource
        locationTrackingMode = LocationTrackingMode.Follow
    }
}

private fun NaverMap.initLocationOverlay(
    hasPermission: Boolean,
    setLocationOverlayState: (LocationOverlay) -> (Unit),
) {
    if (hasPermission) {
        setLocationOverlayState(locationOverlay.apply {
            isVisible = true
            icon = OverlayImage.fromResource(R.drawable.ic_location)
        })
    }
}

@SuppressLint("MissingPermission")
private fun NaverMap.initDeviceLocation(
    hasPermission: Boolean,
    circleOverlay: CircleOverlay,
    fusedLocationClient: FusedLocationProviderClient,
    lastCameraPosition: CameraPosition?,
) {
    if (hasPermission) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                locationOverlay.position = LatLng(location)
                setCircleOverlayLocation(circleOverlay, location)
                initCameraPosition(lastCameraPosition, LocationPoint(location.latitude, location.longitude))
            }
        }
    }
}

private fun NaverMap.initCameraPosition(
    lastCameraPosition: CameraPosition?,
    lastLocation: LocationPoint?
) {
    lastCameraPosition?.let {
        moveCamera(CameraUpdate.toCameraPosition(it))
    } ?: run {
        lastLocation?.let {
            moveCamera(CameraUpdate.scrollTo(LatLng(it.latitude, it.longitude)))
        }
    }
}

private fun NaverMap.setLocationChangeListener(
    circleOverlay: CircleOverlay,
    updateCurLocation: (Double, Double) -> Unit,
) {
    addOnLocationChangeListener { location ->
        setCircleOverlayLocation(circleOverlay, location)
        updateCurLocation(location.latitude, location.longitude)
    }
}

private fun CircleOverlay.initCircleOverlay() {
    color = Purple15.toArgb()
    outlineColor = Primary.toArgb()
    outlineWidth = 3
    radius = CIRCLE_RADIUS_METER
}

private fun NaverMap.setCircleOverlayLocation(circleOverlay: CircleOverlay, location: Location) {
    circleOverlay.center = LatLng(location.latitude, location.longitude)
    if (circleOverlay.map == null) circleOverlay.map = this
}

private fun NaverMap.initMapSettings() {
    mapType = MAP_TYPE
    extent = LatLngBounds(SOUTHWEST_LIMIT, NORTHEAST_LIMIT)
    setCameraZoomLimit()
    uiSettings.setNaverMapUi()
}

private fun UiSettings.setNaverMapUi() {
    isLocationButtonEnabled = true
    isZoomControlEnabled = false
    isTiltGesturesEnabled = false
}

private fun NaverMap.setCameraZoomLimit() {
    minZoom = MIN_ZOOM_LEVEL
    maxZoom = MAX_ZOOM_LEVEL
}

// 지도 클릭 이벤트 설정
private fun NaverMap.setMapClickListener(
    resetSelectedMarkerAndPick: () -> Unit
) {
    this.setOnMapClickListener { _, _ ->
        resetSelectedMarkerAndPick()
    }
}

// 카메라 대기 이벤트 설정
private fun NaverMap.setCameraIdleListener(
    updateCenterLatLng: (LatLng) -> Unit
) {
    addOnCameraIdleListener {
        updateCenterLatLng(cameraPosition.target)
    }
}

private val MAP_TYPE = NaverMap.MapType.Navi
private val SOUTHWEST_LIMIT = LatLng(33.011268, 124.344361)
private val NORTHEAST_LIMIT = LatLng(39.346507, 130.826372)
private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
private const val CIRCLE_RADIUS_METER = 100.0
private const val INITIAL_CAMERA_ZOOM = 16.5
private const val MIN_ZOOM_LEVEL = 6.0
private const val MAX_ZOOM_LEVEL = 18.0
internal const val DEFAULT_MARKER_Z_INDEX = 0
internal const val CLICKED_MARKER_Z_INDEX = 100
