package com.github.factotum_sdp.factotum.model

import com.github.factotum_sdp.factotum.serializers.NullableDateKSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Shift(@Serializable(with = NullableDateKSerializer::class)val date: Date? = null, val user: User = User(), val records: DRecordList = DRecordList(
    emptyList()
)
)