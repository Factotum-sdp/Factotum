package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoggedInUser
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.ui.login.LoggedInUserView
import com.github.factotum_sdp.factotum.ui.login.LoginFormState
import com.github.factotum_sdp.factotum.ui.login.LoginResult
import com.github.factotum_sdp.factotum.ui.login.LoginViewModel
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class LoginViewModelTest {

    /*@Mock
    private lateinit var loginRepository: LoginRepository

    private lateinit var loginViewModel: LoginViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    init {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun `login with valid credentials`() {
        val userEmail = "test@test.com"
        val password = "123456"

        val fakeUser = LoggedInUser("fake_id", userEmail)
        val fakeResult = Result.Success(fakeUser)

        loginRepository = mock(LoginRepository::class.java)
        loginViewModel = LoginViewModel(loginRepository)

        // Mock login method in repository
        `when`(loginRepository.login(userEmail, password)).thenReturn(fakeResult)

        // Call login method in view model
        loginViewModel.login(userEmail, password)

        loginViewModel.loginResult.observeForever {  }

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
    }*/
}