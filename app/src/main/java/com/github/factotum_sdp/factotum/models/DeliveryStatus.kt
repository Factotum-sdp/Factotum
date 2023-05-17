package com.github.factotum_sdp.factotum.models

import java.util.Date

data class DeliveryStatus(
    val destID: String = "",
    val clientID: String = "",
    val timeStamp: Date? = null,
    val addressName: String? = null,
    val latitude : Double? = null,
    val longitude : Double? = null,
)