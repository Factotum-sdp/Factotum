package com.github.factotum_sdp.factotum

import android.view.WindowManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MapsMarkerActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MapsMarkerActivity::class.java)

    @Test
    fun testMapIsDisplayed() {
        // Verify that the map fragment is displayed
        onView(withId(R.id.map))
            .check(matches(isDisplayed()))
    }

    // Custom matcher for matching Toasts
    private class ToastMatcher : TypeSafeMatcher<Root>() {

        override fun describeTo(description: Description?) {
            description?.appendText("is toast")
        }

        override fun matchesSafely(root: Root?): Boolean {
            val type = root?.windowLayoutParams?.get()?.type
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                val windowToken = root.decorView?.windowToken
                val appToken = root.decorView?.applicationWindowToken
                if (windowToken === appToken) {
                    // Means this window isn't contained by any other windows.
                    return true
                }
            }
            return false
        }
    }
}