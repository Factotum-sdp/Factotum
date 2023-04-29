package com.github.factotum_sdp.factotum.utils

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.factotum_sdp.factotum.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.AllOf.allOf

object PreferencesSetting {

    private const val WAIT_TIME_BUTTON = 500L

    fun enableTouchClick() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        // Add an explicit wait
        runBlocking{delay(WAIT_TIME_BUTTON)}

        // If the button has a unique ID, use withId() instead of withText()
        onView(withText(R.string.rb_label_touch_click))
            .check(matches(isDisplayed()))
            .perform(click())
    }
}