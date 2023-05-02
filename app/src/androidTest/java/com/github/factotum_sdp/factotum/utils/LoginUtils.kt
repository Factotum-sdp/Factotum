package com.github.factotum_sdp.factotum.utils


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.factotum_sdp.factotum.R

class LoginUtils {
    companion object {
        fun loginRoutine() {
            onView(withId(R.id.email))
                .perform(typeText("boss@gmail.com"))
            onView(withId(R.id.password))
                .perform(typeText("123456"))
            onView(withId(R.id.fragment_login_directors_parent)).perform(
                closeSoftKeyboard()
            )
            onView(withId(R.id.login))
                .perform(click())
        }
    }
}
