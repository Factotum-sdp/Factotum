package com.github.factotum_sdp.factotum.utils

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R

object PreferencesSetting {

    fun setRoadBookPrefs(testRule: ActivityScenarioRule<MainActivity>) {
        testRule.scenario.onActivity {
            val settings = it.applicationSettingsViewModel()
            settings.updateUseRoadBookPreferences(false)
        }
    }

    fun enableTouchClick() {
        Espresso.openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        Espresso.onView(ViewMatchers.withText(R.string.rb_label_touch_click))
            .perform(ViewActions.click())
    }
}