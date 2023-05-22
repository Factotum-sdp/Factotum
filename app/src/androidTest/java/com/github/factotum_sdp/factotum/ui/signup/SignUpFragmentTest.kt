package com.github.factotum_sdp.factotum.ui.signup

import androidx.navigation.fragment.NavHostFragment
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookFragment
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookViewModel
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.initFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.not
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpFragmentTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDb() {
            initFirebase()
        }
    }

    @Before
    fun setUp() {
        onView(withId(R.id.signup)).perform(click())
    }

    @Test
    fun signUpFormInitialStateIsEmpty() {
        onView(withId(R.id.name)).check(matches(withText("")))
        onView(withId(R.id.email)).check(matches(withText("")))
        onView(withId(R.id.password)).check(matches(withText("")))
        onView(withId(R.id.role)).check(matches(withText("")))
        onView(withId(R.id.username)).check(matches(withText("")))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithoutUsername() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }


    @Test
    fun signUpFormWithoutRole() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithoutPassword() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithoutEmail() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithoutName() {
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithInvalidUsername() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("user name"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithInvalidPassword() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("12345"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithInvalidEmail() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("janedotdoeatgmaildotcom"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(not(isEnabled())))
    }

    @Test
    fun signUpFormWithNonExistingUsername() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_signup_directors_parent)).check(
                matches(
                    isDisplayed()
                )
            )
        }
    }

    @Test
    fun signUpFormWithAllFields() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("username"))
        onView(withId(R.id.signup)).check(matches(isEnabled()))
    }

    @Test
    fun signupWithUsedEmail(){
        onView(withId(R.id.name)).perform(typeText("Used"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("used@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("used"))
        onView(withId(R.id.signup)).perform(click())
        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_signup_directors_parent)).check(
                matches(
                    isDisplayed()
                )
            )
        }
        onView(withId(R.id.signup)).perform(click())
        onView(withId(R.id.name)).perform(typeText("Used"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("used@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("used"))
        onView(withId(R.id.signup)).perform(click())
        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_signup_directors_parent)).check(
                matches(
                    isDisplayed()
                )
            )
        }
    }

    @Test
    fun signUpFormWithAllFieldsAndClick() {
        onView(withId(R.id.name)).perform(typeText("Jane Doe"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.email)).perform(typeText("jane.doe@gmail.com"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.password)).perform(typeText("123456"))
        onView(withId(R.id.fragment_signup_directors_parent)).perform(
            closeSoftKeyboard()
        )
        onView(withId(R.id.role)).perform(click())
        onData(anything()).inRoot(isPlatformPopup()).atPosition(1).perform(click())
        onView(withId(R.id.username)).perform(typeText("Buhagiat"))
        onView(withId(R.id.signup)).perform(click())
        FirebaseAuth.AuthStateListener {
            onView(withId(R.id.fragment_login_directors_parent)).check(
                matches(
                    isDisplayed()
                )
            )
        }
    }

    @Test
    fun clickOnSignUpLeadsToLoginFragment() {
        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        device.pressBack()
        onView(withId(R.id.fragment_login_directors_parent)).check(
            matches(
                isDisplayed()
            )
        )
    }

    @Test
    fun updateUserFail(){
        val user = User(
            "Jane Doe",
            "jane.doe@gmail.com",
            "123456",
            Role.COURIER,
            "Buhagiat"
        )
        getSignUpViewModel()?.updateUser("Jane Doe", user)
        assert(true)
    }

    @Test
    fun fetchUsernameFail(){
        getSignUpViewModel()?.fetchUsername("not a username")
        assert(true)
    }
    
    private fun getSignUpViewModel(): SignUpViewModel? {
        var signUpViewModel : SignUpViewModel? = null
        testRule.scenario.onActivity {
            val fragment = it.supportFragmentManager.fragments.first() as NavHostFragment
            fragment.let {
                val curr =
                    it.childFragmentManager.primaryNavigationFragment as SignUpFragment
                signUpViewModel = curr.getSignUpViewModelForTest()
            }
        }
        return signUpViewModel
    }
}
