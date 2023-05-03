package com.github.factotum_sdp.factotum.placeholder

import android.location.Location
import java.util.Calendar

object LocationsPlaceHolder {

    val EPFL = Location("fake").apply {
        latitude =  46.5191
        longitude = 6.5661
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

    val GENEVA = Location("fake").apply {
        latitude = 46.2044
        longitude = 6.1432
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

    val ROLEX_CENTER = Location("fake").apply {
        latitude = 46.517733
        longitude = 6.569090
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

    val PELICAN = Location("fake").apply {
        latitude = 46.514044
        longitude = 6.572233
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }
}



