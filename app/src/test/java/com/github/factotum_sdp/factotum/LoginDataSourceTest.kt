package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.Role
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.io.IOException


@RunWith(MockitoJUnitRunner::class)
class LoginDataSourceTest {

    @Mock
    private lateinit var loginDataSource: LoginDataSource

    private val userName = "Jane Doe"
    private val userEmail = "jane.doe@gmail.com"
    private val userRole = Role.BOSS
    private val validPassword = "123456"
    private val invalidPassword = "123456789"
    private val profile = User(userName, userEmail, userRole)

    @Test
    fun `login with correct credentials returns a LoggedInUser`() {
        // given
        val expectedUser = User(userName, userEmail, userRole)

        loginDataSource = mock(LoginDataSource::class.java)
        `when`(loginDataSource.login(userName, validPassword, profile)).thenReturn(
            Result.Success(
                expectedUser
            )
        )
        // when
        val result = loginDataSource.login(userName, validPassword, profile)

        // then
        assertThat(result, `is`(Result.Success(expectedUser)))
    }

    @Test
    fun `login with incorrect credentials returns an error`() {
        // given
        val exception = IOException("Error logging in")

        // when
        loginDataSource = mock(LoginDataSource::class.java)
        `when`(loginDataSource.login(userName, invalidPassword, profile)).thenReturn(Result.Error(exception))
        val result = loginDataSource.login(userName, invalidPassword, profile)

        // then
        assertThat(result, `is`(Result.Error(exception)))
    }

    @Test
    fun `retrieve profiles returns a list of users`() {
        // given
        val users = listOf(
            User(userName, userEmail, userRole),
            User("William Taylor", "william.taylor@gmail.com", Role.COURIER),
            User("Helen Bates", "helen.bates@gmail.com", Role.COURIER)
        )

        loginDataSource = mock(LoginDataSource::class.java)

        doReturn(Result.Success(users)).`when`(loginDataSource).retrieveUsersList()
        // when
        val result = loginDataSource.retrieveUsersList()

        // then
        assertThat(
            result, `is`(Result.Success(users))
        )
    }

    @Test
    fun `retrieve profiles returns an error`() {
        // given
        val exception = IOException("Error retrieving profiles")

        loginDataSource = mock(LoginDataSource::class.java)

        // when
        doReturn(Result.Error(exception)).`when`(loginDataSource).retrieveUsersList()
        val result = loginDataSource.retrieveUsersList()

        // then
        assertThat(
            result, `is`(Result.Error(exception))
        )
    }
}