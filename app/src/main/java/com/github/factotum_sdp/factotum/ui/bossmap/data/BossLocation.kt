package com.github.factotum_sdp.factotum.ui.bossmap.data

import com.google.android.gms.maps.model.LatLng

data class BossLocation(
    val uid: String,
    val name: String,
    val position: LatLng
)

