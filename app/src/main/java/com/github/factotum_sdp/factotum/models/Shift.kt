package com.github.factotum_sdp.factotum.models

import com.github.factotum_sdp.factotum.serializers.ShiftDateKSerializer
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Shift(@Serializable(with = ShiftDateKSerializer::class)val date: Date = Date(), val user: User = User(), val records: DRecordList = DRecordList(
    emptyList()
)
)