package com.github.factotum_sdp.factotum.ui.route

import androidx.appcompat.widget.SearchView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RouteFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Before
    fun setUp(){
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(ViewActions.click())
    }

    @Test
    fun buttonNextExists(){
        onView(withId(R.id.button_next)).check(matches(not(doesNotExist())))
    }

    @Test
    fun buttonNextInvisible(){
        onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun buttonNextAppearsWhenPressed(){
        onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        Espresso.onData(Matchers.anything()).inAdapterView(withId(R.id.list_view_routes)).atPosition(0).perform(
            ViewActions.click()
        )
        onView(withId(R.id.button_next)).check(matches(isDisplayed()))
    }
    @Test
    fun buttonRunExists(){
        onView(withId(R.id.button_run)).check(matches(not(doesNotExist())))
    }

    @Test
    fun buttonRunInvisible(){
        onView(withId(R.id.button_run)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun buttonRunAppearsWhenPressed(){
        onView(withId(R.id.button_run)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        Espresso.onData(Matchers.anything()).inAdapterView(withId(R.id.list_view_routes)).atPosition(0).perform(
            ViewActions.click()
        )
        onView(withId(R.id.button_run)).check(matches(isDisplayed()))
    }
    @Test
    fun buttonShowDstDisplayed(){
        onView(withId(R.id.button_all)).check(matches(isDisplayed()))
    }

    @Test
    fun searchBarDisplayed(){
        onView(allOf(withId(R.id.search_bar), isAssignableFrom(SearchView::class.java))).check(matches(isDisplayed()))
    }
}