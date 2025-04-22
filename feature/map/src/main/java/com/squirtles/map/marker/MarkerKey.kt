package com.squirtles.map.marker

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.clustering.ClusteringKey
import com.squirtles.model.Pick

data class MarkerKey(val pick: Pick) : ClusteringKey {
    override fun getPosition() = LatLng(pick.location.latitude, pick.location.longitude)
}
