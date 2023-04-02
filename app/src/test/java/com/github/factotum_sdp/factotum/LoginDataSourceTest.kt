package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoggedInUser
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
    private val userRole = Role.CLIENT
    private val validPassword = "123456"
    private val invalidPassword = "azerty"

    @Test
    fun `login with correct credentials returns a LoggedInUser`() {
        // given
        val expectedUser = LoggedInUser(userName, userEmail, userRole)

        loginDataSource = mock(LoginDataSource::class.java)
        `when`(
            loginDataSource.login(
                userEmail,
                validPassword,
                LoginDataSource.User(userName, userEmail, userRole)
            )
        ).thenReturn(
            Result.Success(
                expectedUser
            )
        )
        // when
        val result = loginDataSource.login(
            userName, validPassword, LoginDataSource.User(userName, userEmail, userRole)
        )

        // then
        assertThat(
            result, `is`(Result.Success(expectedUser))
        )
    }

    @Test
    fun `login with incorrect credentials returns an error`() {
        // given
        val exception = IOException("Error logging in")

        // when
        loginDataSource = mock(LoginDataSource::class.java)
        `when`(
            loginDataSource.login(
                userEmail,
                invalidPassword,
                LoginDataSource.User(userName, userEmail, userRole)
            )
        ).thenReturn(Result.Error(exception))
        val result = loginDataSource.login(
            userName, invalidPassword, LoginDataSource.User(userName, userEmail, userRole)
        )

        // then
        assertThat(
            result, `is`(Result.Error(exception))
        )
    }
}