package com.github.factotum_sdp.factotum.utils

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText

object PreferencesSetting {

    fun setRoadBookPrefs(testRule: ActivityScenarioRule<MainActivity>) {
        testRule.scenario.onActivity {
            val settings = it.applicationSettingsViewModel()
            settings.updateUseRoadBookPreferences(false)
        }
    }
    fun enableTouchClick() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(R.string.rb_label_touch_click)).check(matches(isDisplayed()))
            .perform(click())
    }
}