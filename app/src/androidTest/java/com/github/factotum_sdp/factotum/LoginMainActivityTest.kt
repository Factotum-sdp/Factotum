package com.github.factotum_sdp.factotum

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.action.ViewActions.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.ui.login.LoginFragment
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LoginMainActivityTest {

    @Test
    fun testLoginFragmentIsShown() {
        val scenario = launchFragmentInContainer<LoginFragment>()

        scenario.onFragment { fragment ->
            assertTrue(fragment.isVisible)
        }
    }
}