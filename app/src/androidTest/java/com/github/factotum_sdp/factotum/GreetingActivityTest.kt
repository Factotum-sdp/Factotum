package com.github.factotum_sdp.factotum

import android.content.Context
import android.content.Intent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class GreetingActivityTest {

    private val userName = "Carl"

    @get:Rule
    val composeRule = createEmptyComposeRule()

    @Test
    fun greetingMessageIsCorrectlyDisplayed(){
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GreetingActivity::class.java)
        intent.putExtra(context.getString(R.string.userNameIntentId), userName)

        val act = ActivityScenario.launch<GreetingActivity>(intent)
        composeRule.onNodeWithText(context.getString(R.string.greetingMessage, userName))
        act.close()
    }

    @Test
    fun greetingMessageWithoutIntent(){ // for coverage purpose
        val context: Context = ApplicationProvider.getApplicationContext()
        val act = ActivityScenario.launch(GreetingActivity::class.java)
        composeRule.onNodeWithText(context.getString(R.string.greetingMessage, "null"))
            .assertIsDisplayed()
        act.close()
    }
}