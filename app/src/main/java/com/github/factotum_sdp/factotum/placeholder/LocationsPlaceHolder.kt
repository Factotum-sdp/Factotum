package com.github.factotum_sdp.factotum.placeholder

import android.location.Location
import java.util.Calendar

object LocationsPlaceHolder {

    val PELICAN = Location("fake").apply {
        latitude = 46.514044
        longitude = 6.572233
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

    val PELICAN_TO_ROLEX_1 = Location("fake").apply {
        latitude = 46.514899
        longitude = 6.571756
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

    val PELICAN_TO_ROLEX_2 = Location("fake").apply {
        latitude = 46.51659708501517
        longitude = 6.570479266144693
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

    val FRONT_OF_ROLEX = Location("fake").apply {
        latitude = 46.517719 //Dist to ROLEX < 15m
        longitude = 6.569090
        time = Calendar.getInstance().timeInMillis
        accuracy = 10.0f
    }

}



