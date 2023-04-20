package com.github.factotum_sdp.factotum.utils

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import java.util.*

class LocationUtils {
    companion object {
        fun maybeLocationPermission() : Boolean {
            val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            val buttonTextAllow = when (Locale.getDefault().language) {
                Locale.FRENCH.language -> "Uniquement cette fois-ci"
                else -> "Only this time"
            }
            val hasPopup = device.wait(Until.hasObject(By.textContains(buttonTextAllow)), 1000)
            if (hasPopup) {
                device.findObject(By.textContains(buttonTextAllow)).click()
                return true
            }
        return false
        }
    }
}