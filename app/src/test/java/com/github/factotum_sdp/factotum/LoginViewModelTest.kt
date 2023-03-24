package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoggedInUser
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.ui.login.LoggedInUserView
import com.github.factotum_sdp.factotum.ui.login.LoginFormState
import com.github.factotum_sdp.factotum.ui.login.LoginResult
import com.github.factotum_sdp.factotum.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class LoginViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginRepository: LoginRepository

    @BeforeEach
    fun setup() {
        loginRepository = mock(LoginRepository::class.java)
        Dispatchers.setMain(Dispatchers.Unconfined)
        MockitoAnnotations.openMocks(this)
        loginViewModel = LoginViewModel(loginRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials`() {
        val userEmail = "test@test.com"
        val password = "123456"

        val fakeUser = LoggedInUser("fake_id", userEmail)
        val fakeResult = Result.Success(fakeUser)

        // Mock login method in repository
        `when`(loginRepository.login(userEmail, password)).thenReturn(fakeResult)

        // Call login method in view model
        loginViewModel.login(userEmail, password)

        // Verify that loginResult LiveData emits the correct value
        val expected = LoginResult(success = LoggedInUserView(fakeUser.displayName, fakeUser.email))
        assertEquals(expected, loginViewModel.loginResult.value)
    }

    @Test
    fun `login with invalid credentials`() {
        val userEmail = "test@test.com"
        val password = "123"

        // Call login method in view model
        loginViewModel.login(userEmail, password)

        // Verify that loginResult LiveData emits the correct value
        val expected = LoginResult(error = R.string.login_failed)
        assertEquals(expected, loginViewModel.loginResult.value)
    }

    @Test
    fun `login with invalid username`() {
        val username = "invalid_username"
        val password = "123456"

        // Call loginDataChanged method in view model
        loginViewModel.loginDataChanged(username, password)

        // Verify that loginFormState LiveData emits the correct value
        val expected = LoginFormState(usernameError = R.string.invalid_username)
        assertEquals(expected, loginViewModel.loginFormState.value)
    }

    @Test
    fun `login with invalid password`() {
        val username = "test@test.com"
        val password = "123"

        // Call loginDataChanged method in view model
        loginViewModel.loginDataChanged(username, password)

        // Verify that loginFormState LiveData emits the correct value
        val expected = LoginFormState(passwordError = R.string.invalid_password)
        assertEquals(expected, loginViewModel.loginFormState.value)
    }

    @Test
    fun `login with valid credentials should update loginFormState`() {
        val userEmail = "test@test.com"
        val password = "123456"

        // Call loginDataChanged method in view model
        loginViewModel.loginDataChanged(userEmail, password)

        // Verify that loginFormState LiveData emits the correct value
        val expected = LoginFormState(isDataValid = true)
        assertEquals(expected, loginViewModel.loginFormState.value)
    }
}