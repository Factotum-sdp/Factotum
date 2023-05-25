package com.github.factotum_sdp.factotum.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import java.util.concurrent.atomic.AtomicBoolean

object PreferencesSetting {
    fun setRoadBookPrefs(testRule: ActivityScenarioRule<MainActivity>) {
        testRule.scenario.onActivity {
            val settings = it.applicationSettingsViewModel()
            settings.updateUseRoadBookPreferences(false)
        }
    }

    fun enableTouchClick() {
        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())

        val touchClickLabel = ApplicationProvider.getApplicationContext<Context>().getString(R.string.rb_label_touch_click)
        val touchClickOption = onView(withText(touchClickLabel))

        // Check if the touch click option is already activated
        touchClickOption.check(matches(isDisplayed()))
        if (!isChecked(touchClickOption)) {
            touchClickOption.perform(click())
        }
    }

    fun isChecked(viewInteraction: ViewInteraction): Boolean {
        val isCheckedMatcher = isChecked()
        val checked = AtomicBoolean(false)
        viewInteraction.check { view, _ ->
            checked.set(isCheckedMatcher.matches(view))
        }
        return checked.get()
    }


}