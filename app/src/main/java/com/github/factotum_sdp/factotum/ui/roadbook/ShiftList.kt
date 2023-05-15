package com.github.factotum_sdp.factotum.ui.roadbook

import com.github.factotum_sdp.factotum.models.Shift
import kotlinx.serialization.Serializable

@Serializable
data class ShiftList(val shifts: List<Shift>) : List<Shift> by shifts {

    fun add(shift: Shift): ShiftList {
        return ShiftList(shifts + shift)
    }
}