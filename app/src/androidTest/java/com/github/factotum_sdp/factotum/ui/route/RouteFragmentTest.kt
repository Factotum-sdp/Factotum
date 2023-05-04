package com.github.factotum_sdp.factotum.ui.route

import android.Manifest
import android.location.Geocoder
import android.os.Build
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
class RouteFragmentTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            initFirebase()
        }
    }

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @get:Rule
    val permission = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var decorView: View? = null

    @Before
    fun setUp() {
        GeneralUtils.injectBossAsLoggedInUser(testRule)
        onView(withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(withId(R.id.routeFragment))
            .perform(click())
        testRule.scenario.onActivity { activity ->
            decorView = activity.window.decorView
        }
    }

    @Test
    fun buttonNextExists() {
        onView(withId(R.id.button_next)).check(matches(not(doesNotExist())))
    }

    @Test
    fun buttonNextInvisible() {
        onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun buttonNextAppearsWhenPressed() {
        onView(withId(R.id.button_next)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        Espresso.onData(anything()).inAdapterView(withId(R.id.list_view_routes)).atPosition(0)
            .perform(
                click()
            )
        onView(withId(R.id.button_next)).check(matches(isDisplayed()))
    }

    @Test
    fun buttonRunExists() {
        onView(withId(R.id.button_run)).check(matches(not(doesNotExist())))
    }

    @Test
    fun buttonRunInvisible() {
        onView(withId(R.id.button_run)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
    }

    @Test
    fun buttonRunAppearsWhenPressed() {
        onView(withId(R.id.button_run)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        Espresso.onData(anything()).inAdapterView(withId(R.id.list_view_routes)).atPosition(0)
            .perform(
                click()
            )
        onView(withId(R.id.button_run)).check(matches(isDisplayed()))
    }

    @Test
    fun buttonShowDstDisplayed() {
        onView(withId(R.id.button_all)).check(matches(isDisplayed()))
    }

    @Test
    fun searchBarDisplayed() {
        onView(allOf(withId(R.id.search_bar), isAssignableFrom(SearchView::class.java))).check(
            matches(isDisplayed())
        )
    }

    @Test
    fun validSearchShowsAddressSnackBar() {
        val city = "Lausanne"
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText(city))
            .perform(pressKey(KeyEvent.KEYCODE_ENTER))
        val geocoder = Geocoder(getApplicationContext())
        var result: String
        val latch = CountDownLatch(1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(city, 1) { addresses ->
                val bestAddress = addresses[0]
                result = bestAddress.getAddressLine(0).toString()
                onView(withId(com.google.android.material.R.id.snackbar_text)).check(
                    matches(
                        withText(result)
                    )
                )
                latch.countDown()
            }
            latch.await()
        } else {
            val bestAddresses = geocoder.getFromLocationName(city, 1)
            result = bestAddresses?.get(0).toString()
            onView(withId(com.google.android.material.R.id.snackbar_text))
                .check(matches(withText(result)))
        }
    }
    /*
    @Test
    fun wrongSearchShowsNoResultSnackbar(){
    val city = "wrong_search"
    onView(withId(androidx.appcompat.R.id.search_src_text)).perform(typeText(city)).perform(pressKey(KeyEvent.KEYCODE_ENTER))
    onView(withId(com.google.android.material.R.id.snackbar_text))
    .check(matches(withText(NO_RESULT)))
    }
     */

}
