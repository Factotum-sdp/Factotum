package com.github.factotum_sdp.factotum

import androidx.annotation.StringRes
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import org.hamcrest.core.AllOf.allOf


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val userName = "Carl"

    @get:Rule
    var composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userNameEditTextLabelIsDisplayed() {
        composeRule
            .onNodeWithText(strRes(R.string.userNameTextFieldLabel))
            .assertIsDisplayed()
    }

    @Test
    fun validateButtonIsDisplayed() {
        composeRule
            .onNodeWithText(strRes(R.string.validateButton))
            .assertIsDisplayed()
    }

    @Test
    fun userNameIsDisplayedAfterEdition() {
        performUserNameEdit()
        composeRule.onNodeWithText(userName).assertIsDisplayed()
    }

    @Test
    fun intentIsCorrectlyFired() {
        Intents.init()
        performUserNameEdit()
        composeRule.onNodeWithText("Validate").performClick()
        intended(
            allOf(
                hasComponent(GreetingActivity::class.java.name),
                hasExtra(strRes(R.string.userNameIntentId), userName)
            )
        )
        Intents.release()
    }

    @Test
    fun endToEndGreetingMessage() {
        performUserNameEdit()
        composeRule.onNodeWithText(strRes(R.string.validateButton)).performClick()
        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.greetingMessage, userName))
            .assertIsDisplayed()
    }

    @Test
    fun endToEndGreetingMessageWithoutUserName() {
        composeRule.onNodeWithText(strRes(R.string.validateButton)).performClick()
        composeRule
            .onNodeWithText(composeRule.activity.getString(R.string.greetingMessage, ""))
            .assertIsDisplayed()
    }

    private fun performUserNameEdit() {
        composeRule
            .onNodeWithText(strRes(R.string.userNameTextFieldLabel))
            .performTextInput(userName)
        closeSoftKeyboard()
    }

    private fun strRes(@StringRes resId: Int): String {
        return composeRule.activity.getString(resId)
    }
}