package com.github.factotum_sdp.factotum.utils

import android.app.Activity
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.factotum_sdp.factotum.R
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LoginMenuIdlingResource(private val activity: Activity) : IdlingResource {
    private val maxAttempts = 3
    private val maxWaitingTimePerAttemptInSeconds: Long = 2
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private val countDownLatch = CountDownLatch(1)

    override fun getName(): String = "LoginMenuIdlingResource"

    override fun isIdleNow(): Boolean {
        val isIdle = activity.findViewById<View>(R.id.fragment_login_directors_parent)?.visibility != View.VISIBLE

        if (isIdle) {
            resourceCallback?.onTransitionToIdle()
            countDownLatch.countDown()
        }

        return isIdle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
        Thread {
            try {
                var success = false
                var attempts = 0
                while (!success && attempts < maxAttempts) {
                    success = countDownLatch.await(maxWaitingTimePerAttemptInSeconds, TimeUnit.SECONDS)
                    if (!success) {
                        // Timeout occurred, retry login
                        attempts++
                        if (attempts < maxAttempts) {
                            retryLogin()
                        }
                    }
                }
                callback?.onTransitionToIdle()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun retryLogin() {
        activity.runOnUiThread {
            onView(withId(R.id.login)).perform(click())
        }
        countDownLatch.await(maxWaitingTimePerAttemptInSeconds, TimeUnit.SECONDS)
    }
}
