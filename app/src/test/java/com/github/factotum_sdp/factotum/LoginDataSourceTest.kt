package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.Role
import com.github.factotum_sdp.factotum.data.User
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
        val expectedResult = "Success"

        loginDataSource = mock(LoginDataSource::class.java)
        `when`(loginDataSource.login(userName, validPassword)).thenReturn(
            Result.Success(
                expectedResult
            )
        )
        // when
        val result = loginDataSource.login(userName, validPassword)

        // then
        assertThat(result, `is`(Result.Success(expectedResult)))
    }

    @Test
    fun `login with incorrect credentials returns an error`() {
        // given
        val exception = IOException("Error logging in")

        // when
        loginDataSource = mock(LoginDataSource::class.java)
        `when`(loginDataSource.login(userName, invalidPassword)).thenReturn(
            Result.Error(
                exception
            )
        )
        val result = loginDataSource.login(userName, invalidPassword)

        // then
        assertThat(result, `is`(Result.Error(exception)))
    }

    @Test
    fun `retrieve profiles returns a list of users`() {
        // given
        val user = User(userName, userEmail, userRole)

        loginDataSource = mock(LoginDataSource::class.java)

        doReturn(Result.Success(user)).`when`(loginDataSource).retrieveUser(userEmail)
        // when
        val result = loginDataSource.retrieveUser(userEmail)

        // then
        assertThat(
            result, `is`(Result.Success(user))
        )
    }

    @Test
    fun `retrieve profiles returns an error`() {
        // given
        val exception = IOException("Error retrieving profiles")

        loginDataSource = mock(LoginDataSource::class.java)

        // when
        doReturn(Result.Error(exception)).`when`(loginDataSource).retrieveUser(userEmail)
        val result = loginDataSource.retrieveUser(userEmail)

        // then
        assertThat(
            result, `is`(Result.Error(exception))
        )
    }
}