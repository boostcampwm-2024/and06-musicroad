package com.squirtles.musicroad.map

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.overlay.Marker
import com.squirtles.domain.model.Pick
import com.squirtles.domain.usecase.location.GetLastLocationUseCase
import com.squirtles.domain.usecase.location.SaveLastLocationUseCase
import com.squirtles.domain.usecase.pick.FetchPickUseCase
import com.squirtles.domain.usecase.user.GetCurrentUidUseCase
import com.squirtles.musicroad.map.marker.MarkerKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _centerLatLng: MutableStateFlow<LatLng?> = MutableStateFlow(null)
    val centerLatLng = _centerLatLng.asStateFlow()

    private var _lastCameraPosition: CameraPosition? = null
    val lastCameraPosition get() = _lastCameraPosition

    private val _picks: MutableMap<String, Pick> = mutableMapOf() // key: pickId, value: Pick
    val picks: Map<String, Pick> get() = _picks

    private val _nearPicks = MutableStateFlow<List<Pick>>(emptyList())
    val nearPicks = _nearPicks.asStateFlow()

    private val _clickedMarkerState = MutableStateFlow(MarkerState())
    val clickedMarkerState = _clickedMarkerState.asStateFlow()

    // FIXME : 네이버맵의 LocationChangeListener에서 실시간으로 변하는 위치 정보 -> 더 나은 방법이 있으면 고쳐주세요
    private var _currentLocation: Location? = null
    val curLocation get() = _currentLocation

    // LocalDataSource에 저장되는 위치 정보
    // Firestore 데이터 쿼리 작업 최소화 및 위치데이터 공유 용도
    val lastLocation: StateFlow<Location?> = getLastLocationUseCase()

    fun getUid() = getCurrentUidUseCase()

    fun setLastCameraPosition(cameraPosition: CameraPosition) {
        _lastCameraPosition = cameraPosition
    }

    fun updateCurLocation(location: Location) {
        _currentLocation = location

        if (lastLocation.value == null
            || calculateDistance(location.latitude, location.longitude) > 5.0
        ) {
            saveCurLocation(location)
        }
    }

    private fun saveCurLocation(location: Location) {
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
        from: Location? = lastLocation.value,
    ): Double {
        return from?.let {
            val location = Location("pickLocation").apply {
                latitude = lat
                longitude = lng
            }
            from.distanceTo(location).toDouble()
        } ?: -1.0
    }

    fun updateCenterLatLng(latLng: LatLng) {
        _centerLatLng.value = latLng
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
            if (prevClickedMarker == marker) return@launch

            prevClickedMarker?.toggleSizeByClick(context, false)
            marker.toggleSizeByClick(context, true)
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

    fun fetchPicksInBounds(leftTop: LatLng, clusterer: Clusterer<MarkerKey>?) {
        viewModelScope.launch {
            _centerLatLng.value?.run {
                val radiusInM = leftTop.distanceTo(this)
                fetchPickUseCase(this.latitude, this.longitude, radiusInM)
                    .onSuccess { pickList ->
                        val newKeyTagMap: MutableMap<MarkerKey, String> = mutableMapOf()
                        pickList.forEach { pick ->
                            newKeyTagMap[MarkerKey(pick)] = pick.id
                            _picks[pick.id] = pick
                        }
                        _clickedMarkerState.value.clusterPickList?.let { clusterPickList -> // 클러스터 마커가 선택되어 있는 경우
                            val updatedPickList = mutableListOf<Pick>()
                            clusterPickList.forEach { pick ->
                                _picks[pick.id]?.let { updatedPick ->
                                    updatedPickList.add(updatedPick)
                                }
                            }
                            _clickedMarkerState.emit(_clickedMarkerState.value.copy(clusterPickList = updatedPickList.toList())) // 최신 픽 정보로 clusterPickList 업데이트
                        }
                        clusterer?.addAll(newKeyTagMap)
                    }
                    .onFailure {
                        // TODO: NoSuchPickInRadiusException일 때
                        Log.e("MapViewModel", "${it.message}")
                    }
            }
        }
    }

    fun requestPickNotificationArea(location: Location, notiRadius: Double) {
        viewModelScope.launch {
            fetchPickUseCase(location.latitude, location.longitude, notiRadius)
                .onSuccess {
                    _nearPicks.emit(it)
                }.onFailure {
                    _nearPicks.emit(emptyList())
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
