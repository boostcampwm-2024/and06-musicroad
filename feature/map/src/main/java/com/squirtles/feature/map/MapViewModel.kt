package com.squirtles.feature.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.overlay.Marker
import com.squirtles.core.model.LocationPoint
import com.squirtles.core.model.Pick
import com.squirtles.domain.location.usecase.GetLastLocationUseCase
import com.squirtles.domain.location.usecase.SaveLastLocationUseCase
import com.squirtles.domain.pick.usecase.FetchPickUseCase
import com.squirtles.domain.user.usecase.GetCurrentUidUseCase
import com.squirtles.feature.map.marker.MarkerKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MarkerState(
    val prevClickedMarker: Marker? = null, // 이전에 클릭한 마커(클러스터 마커 & 단말 마커)
    val clusterPickList: List<Pick>? = null, // 클러스터 마커의 픽 정보
    val curPickId: String? = null // 현재 선택한 마커의 pick id
)

@HiltViewModel
class MapViewModel @Inject constructor(
    getLastLocationUseCase: GetLastLocationUseCase,
    private val saveLastLocationUseCase: SaveLastLocationUseCase,
    private val fetchPickUseCase: FetchPickUseCase,
    private val getCurrentUidUseCase: GetCurrentUidUseCase
) : ViewModel() {

    private val _centerPoint: MutableStateFlow<LocationPoint?> = MutableStateFlow(null)
    val centerPoint = _centerPoint.asStateFlow()

    private var _lastCameraPosition: CameraPosition? = null
    val lastCameraPosition get() = _lastCameraPosition

    private val _picks: MutableMap<String, Pick> = mutableMapOf() // key: pickId, value: Pick
    val picks: Map<String, Pick> get() = _picks

    private val _nearPicks = MutableStateFlow<List<Pick>>(emptyList())
    val nearPicks = _nearPicks.asStateFlow()

    private val _clickedMarkerState = MutableStateFlow(MarkerState())
    val clickedMarkerState = _clickedMarkerState.asStateFlow()

    private val _fetchPicksErrorToast = MutableSharedFlow<Unit>()
    val fetchPicksErrorToast = _fetchPicksErrorToast.asSharedFlow()

    // FIXME : 네이버맵의 LocationChangeListener에서 실시간으로 변하는 위치 정보 -> 더 나은 방법이 있으면 고쳐주세요
    private var _currentLocation: LocationPoint? = null

    // LocalDataSource에 저장되는 위치 정보
    // Firestore 데이터 쿼리 작업 최소화 및 위치데이터 공유 용도
    val lastLocation: StateFlow<LocationPoint?> = getLastLocationUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3000L),
        initialValue = null
    )

    fun getUid() = getCurrentUidUseCase()

    fun setLastCameraPosition(cameraPosition: CameraPosition) {
        _lastCameraPosition = cameraPosition
    }

    fun updateCurLocation(lat: Double, lng: Double) {
        val point = LocationPoint(lat, lng)
        _currentLocation = point

        if (lastLocation.value == null
            || calculateDistance(lat, lng) > 5.0
        ) {
            saveCurLocation(point)
        }
    }

    private fun saveCurLocation(location: LocationPoint) {
        viewModelScope.launch {
            saveLastLocationUseCase(location)
        }
    }

    fun saveCurLocationForced() {
        _currentLocation?.let { location ->
            saveCurLocation(location)
        }
    }

    fun calculateDistance(
        lat: Double,
        lng: Double,
        from: LocationPoint? = lastLocation.value,
    ): Double {
        return from?.let {
            val pickLatLng = LatLng(lat, lng)
            LatLng(from.latitude, from.longitude).distanceTo(pickLatLng)
        } ?: -1.0
    }

    fun updateCenterLocation(lat: Double, lng: Double) {
        _centerPoint.value = LocationPoint(lat, lng)
    }

    // FIXME: 인자로 Context 받는 것 수정하기
    fun setClickedMarker(context: Context, marker: Marker) {
        viewModelScope.launch {
            marker.toggleSizeByClick(context, true)
            _clickedMarkerState.emit(_clickedMarkerState.value.copy(prevClickedMarker = marker))
        }
    }

    fun setClickedMarkerState(
        context: Context,
        marker: Marker,
        clusterTag: String? = null,
        pickId: String? = null
    ) {
        viewModelScope.launch {
            val prevClickedMarker = _clickedMarkerState.value.prevClickedMarker
            // 클릭한 마커와 클릭되어 있는 마커가 다를 때만 크기 변경
            if (prevClickedMarker != marker) {
                prevClickedMarker?.toggleSizeByClick(context, false)
                marker.toggleSizeByClick(context, true)
            }

            val pickList = clusterTag?.split(",")?.mapNotNull { id -> picks[id] }
            _clickedMarkerState.emit(MarkerState(marker, pickList, pickId))
        }
    }

    fun resetClickedMarkerState(context: Context) {
        viewModelScope.launch {
            val prevClickedMarker = _clickedMarkerState.value.prevClickedMarker
            prevClickedMarker?.toggleSizeByClick(context, false)
            _clickedMarkerState.emit(MarkerState(null, null, null))
        }
    }

    // 유저 화면 내 픽 불러오기
    fun fetchPicksInBounds(leftTop: LatLng, clusterer: Clusterer<MarkerKey>?) {
        viewModelScope.launch {
            _centerPoint.value?.run {
                val radiusInM = leftTop.distanceTo(LatLng(this.latitude, this.longitude))
                fetchPickUseCase(this.latitude, this.longitude, radiusInM)
                    .catch {
                        _fetchPicksErrorToast.emit(Unit)
                    }
                    .collect { pickList ->
                        val newKeyTagMap: MutableMap<MarkerKey, String> = mutableMapOf()
                        pickList.forEach { pick ->
                            newKeyTagMap[MarkerKey(pick)] = pick.id
                            _picks[pick.id] = pick
                        }

                        // 업데이트된 리스트에 기존 픽이 없으면 _picks와 clusterer에서 삭제
                        val deletedKeyList = _picks.keys
                            .filterNot { it in newKeyTagMap.values }
                            .mapNotNull { pickId ->
                                _picks.remove(pickId)?.let { MarkerKey(it) }
                            }

                        clusterer?.addAll(newKeyTagMap)
                        clusterer?.removeAll(deletedKeyList)
                    }
            }
        }
    }

    // CircleOverlay 내 픽 불러오기
    fun requestPickNotificationArea(lat: Double, lng: Double, notifyRadius: Double) {
        viewModelScope.launch {
            fetchPickUseCase(lat, lng, notifyRadius)
                .catch {
                    _fetchPicksErrorToast.emit(Unit)
                }
                .collect { pickList ->
                    _nearPicks.emit(pickList)
                }
        }
    }

    private fun Marker.toggleSizeByClick(context: Context, isClicked: Boolean) {
        val defaultIconWidth = this.icon.getIntrinsicWidth(context)
        val defaultIconHeight = this.icon.getIntrinsicHeight(context)

        zIndex = if (isClicked) CLICKED_MARKER_Z_INDEX else DEFAULT_MARKER_Z_INDEX
        this.width =
            if (isClicked) (defaultIconWidth * MARKER_SCALE).toInt() else defaultIconWidth
        this.height =
            if (isClicked) (defaultIconHeight * MARKER_SCALE).toInt() else defaultIconHeight
    }

    companion object {
        private const val MARKER_SCALE = 1.5
    }
}
