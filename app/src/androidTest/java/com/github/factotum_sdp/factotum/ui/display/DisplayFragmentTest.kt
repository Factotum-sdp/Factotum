package com.github.factotum_sdp.factotum.ui.display

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayFragmentTest {

    private lateinit var scenario: FragmentScenario<DisplayFragment>

    @Before
    fun setUp() {
        scenario = launchFragmentInContainer(themeResId = R.style.Theme_Factotum)
    }

    @Test
    fun displayFragment_uiElementsDisplayed() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.refreshButton)).check(matches(isDisplayed()))
    }

    @Test
    fun displayFragment_recyclerViewHasCorrectLayoutManager() {
        scenario.onFragment { fragment ->
            val recyclerView = fragment.requireView().findViewById<RecyclerView>(R.id.recyclerView)
            assert(recyclerView.layoutManager is LinearLayoutManager)
        }
    }

    @Test
    fun displayFragment_refreshButtonClicked() {
        onView(withId(R.id.refreshButton)).perform(click())
    }
}
