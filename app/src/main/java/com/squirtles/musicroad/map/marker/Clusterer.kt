package com.squirtles.musicroad.map.marker

import android.content.Context
import android.graphics.PointF
import android.view.View
import androidx.compose.ui.graphics.toArgb
import com.naver.maps.map.clustering.ClusterMarkerInfo
import com.naver.maps.map.clustering.Clusterer
import com.naver.maps.map.clustering.ClusteringKey
import com.naver.maps.map.clustering.DefaultClusterMarkerUpdater
import com.naver.maps.map.clustering.DefaultLeafMarkerUpdater
import com.naver.maps.map.clustering.DefaultMarkerManager
import com.naver.maps.map.clustering.LeafMarkerInfo
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.squirtles.musicroad.map.DEFAULT_MARKER_Z_INDEX
import com.squirtles.musicroad.map.MapViewModel
import com.squirtles.musicroad.map.setCameraToMarker
import com.squirtles.musicroad.ui.theme.Black
import com.squirtles.musicroad.ui.theme.Blue
import com.squirtles.musicroad.ui.theme.Primary
import com.squirtles.musicroad.ui.theme.White

internal fun <T : ClusteringKey> buildClusterer(
    context: Context,
    mapViewModel: MapViewModel,
): Clusterer<T> {
    return Clusterer.ComplexBuilder<T>()
        .thresholdStrategy { zoom ->
            when {
                zoom >= 17.0 -> 15.0
                zoom >= 16.0 -> 16.0
                zoom >= 15.0 -> 17.0
                zoom >= 14.0 -> 20.0
                zoom >= 13.5 -> 25.0
                zoom >= 13.0 -> 30.0
                zoom >= 12.5 -> 35.0
                zoom >= 12.0 -> 40.0
                zoom >= 11.0 -> 45.0
                zoom >= 9.0 -> 40.0
                zoom >= 7.5 -> 21.0
                zoom >= 7.0 -> 18.0
                else -> 15.0
            }
        }
        .tagMergeStrategy { cluster ->
            cluster.children.map { it.tag }.joinToString(",")
        }
        .markerManager(object : DefaultMarkerManager() {
            override fun createMarker(): Marker {
                val marker = Marker()
                with(marker) {
                    icon = OverlayImage.fromView(View(context))
                    setCaptionAligns(Align.Center)
                    captionHaloColor = android.graphics.Color.TRANSPARENT
                }
                return marker
            }
        })
        .clusterMarkerUpdater(object : DefaultClusterMarkerUpdater() {
            override fun updateClusterMarker(info: ClusterMarkerInfo, marker: Marker) {
                // 클릭된 마커가 클러스터 마커 안에 포함되면 클릭된 마커 해제
                mapViewModel.clickedMarkerState.value.curPickId?.let { curPickId ->
                    if (info.tag.toString().contains(curPickId)) {
                        mapViewModel.resetClickedMarkerState(context)
                    }
                }
                val densityType = when {
                    info.size < 10 -> DensityType.LOW
                    info.size < 100 -> DensityType.MEDIUM
                    else -> DensityType.HIGH
                }
                val captionColor = when {
                    densityType == DensityType.LOW -> Black
                    else -> White
                }
                val clusterMarkerIconView = ClusterMarkerIconView(context, densityType)
                marker.icon = OverlayImage.fromView(clusterMarkerIconView)
                marker.zIndex = DEFAULT_MARKER_Z_INDEX
                marker.anchor = PointF(0.5F, 0.5F)
                marker.captionText = info.size.toString()
                marker.captionColor = captionColor.toArgb()
                marker.onClickListener = Overlay.OnClickListener {
                    marker.map?.let { map ->
                        setCameraToMarker(
                            map = map,
                            clickedMarkerPosition = marker.position
                        )
                        mapViewModel.setClickedMarkerState(
                            context = context,
                            marker = marker,
                            clusterTag = info.tag.toString()
                        )
                    }
                    true
                }
                // 클러스터 마커를 클릭한 채로 configuration change 시 크기 유지
                if (info.tag.toString()
                    == mapViewModel.clickedMarkerState.value.clusterPickList?.joinToString(",") { it.id }
                ) {
                    mapViewModel.setClickedMarker(context, marker)
                }
            }
        })
        .leafMarkerUpdater(object : DefaultLeafMarkerUpdater() {
            override fun updateLeafMarker(info: LeafMarkerInfo, marker: Marker) {
                marker.anchor = Marker.DEFAULT_ANCHOR
                marker.zIndex = DEFAULT_MARKER_Z_INDEX
                marker.captionText = ""

                val pick = (info.key as MarkerKey).pick
                val leafMarkerIconView = LeafMarkerIconView(context).apply {
                    val color = if (pick.createdBy.uid == mapViewModel.getUid()) Blue else Primary
                    setPaintColor(color.toArgb())
                }
                leafMarkerIconView.setLeafMarkerIcon(pick) {
                    marker.icon = OverlayImage.fromView(leafMarkerIconView)
                    marker.setOnClickListener {
                        marker.map?.let { map ->
                            setCameraToMarker(
                                map = map,
                                clickedMarkerPosition = marker.position
                            )
                            mapViewModel.setClickedMarkerState(
                                context = context,
                                marker = marker,
                                pickId = pick.id
                            )
                        }
                        true
                    }
                    // 단말 마커를 클릭한 채로 configuration change 시 크기 유지
                    if (pick.id == mapViewModel.clickedMarkerState.value.curPickId) {
                        mapViewModel.setClickedMarker(context, marker)
                    }
                }
            }
        })
        .build()
}
