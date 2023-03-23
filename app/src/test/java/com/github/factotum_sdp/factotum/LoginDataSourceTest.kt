package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.LoggedInUser
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

    private val username = "Jane Doe"
    private val useremail = "jane.doe@gmail.com"
    private val validPassword = "123456"
    private val invalidPassword = "azerty"

    @Test
    fun `login with correct credentials returns a LoggedInUser`() {
        // given
        val expectedUser = LoggedInUser(username, useremail)

        loginDataSource = mock(LoginDataSource::class.java)
        `when`(loginDataSource.login(username, validPassword)).thenReturn(
            Result.Success(
                expectedUser
            )
        )
        // when
        val result = loginDataSource.login(username, validPassword)

        // then
        assertThat(result, `is`(Result.Success(expectedUser)))
    }

    @Test
    fun `login with incorrect credentials returns an error`() {
        // given
        val exception = IOException("Error logging in")

        // when
        loginDataSource = mock(LoginDataSource::class.java)
        `when`(loginDataSource.login(username, invalidPassword)).thenReturn(Result.Error(exception))
        val result = loginDataSource.login(username, invalidPassword)

        // then
        assertThat(result, `is`(Result.Error(exception)))
    }
}