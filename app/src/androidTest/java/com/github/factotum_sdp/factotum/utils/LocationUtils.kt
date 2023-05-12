package com.github.factotum_sdp.factotum.utils

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import java.util.*

class LocationUtils {
    companion object {
        val buttonTextAllow = when (Locale.getDefault().language) {
            Locale.FRENCH.language -> "Uniquement cette fois-ci"
            else -> "Only this time"
        }

        fun hasLocationPopUp(): Boolean {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            return device.wait(Until.hasObject(By.textContains(buttonTextAllow)), 1000)
        }

    }
}