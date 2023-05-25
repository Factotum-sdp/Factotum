package com.github.factotum_sdp.factotum.models

import com.github.factotum_sdp.factotum.serializers.DateKSerializer
import kotlinx.serialization.Serializable
import java.util.Date


data class DeliveryStatus(
    val courier : String = "",
    val destID: String = "",
    val clientID: String = "",
    val timeStamp: Date? = null,
    val addressName: String? = null,
    val latitude : Double? = null,
    val longitude : Double? = null,
)