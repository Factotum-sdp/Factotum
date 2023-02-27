package com.github.schuetzcarl.bootcamp

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {

    @Test
    fun correctGreetingMessageIsDisplayed(){
        val intent = Intent(ApplicationProvider.getApplicationContext(), GreetingActivity::class.java)
        intent.putExtra("userName", "Carl")

        val act = ActivityScenario.launch<GreetingActivity>(intent)
        onView(withId(R.id.greetingMessage))
            .check(matches(withText("Hello Carl")))
        act.close()
    }
}