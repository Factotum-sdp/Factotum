package com.github.factotum_sdp.factotum.utils

import android.app.Activity
import android.view.View
import androidx.test.espresso.IdlingResource
import com.github.factotum_sdp.factotum.R
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LoginMenuIdlingResource(private val activity: Activity) : IdlingResource {
    private val maxWaitingTimeInSeconds: Long = 10
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
                // Wait for the maximum waiting time or until the countDownLatch reaches zero
                val success = countDownLatch.await(maxWaitingTimeInSeconds, TimeUnit.SECONDS)
                if (!success) {
                    // Timeout occurred
                    callback?.onTransitionToIdle()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }
}
