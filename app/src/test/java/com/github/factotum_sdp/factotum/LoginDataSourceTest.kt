package com.github.factotum_sdp.factotum

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

    @Test
    fun `retrieve profiles returns a list of users`() {
        // given
        val users = listOf(
            LoginDataSource.User(userName, userEmail, userRole),
            LoginDataSource.User("William Taylor", "william.taylor@gmail.com", Role.COURIER),
            LoginDataSource.User("Helen Bates", "helen.bates@gmail.com", Role.COURIER)
        )

        loginDataSource = mock(LoginDataSource::class.java)

        doReturn(Result.Success(users)).`when`(loginDataSource).retrieveProfiles()
        // when
        val result = loginDataSource.retrieveProfiles()

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
        doReturn(Result.Error(exception)).`when`(loginDataSource).retrieveProfiles()
        val result = loginDataSource.retrieveProfiles()

        // then
        assertThat(
            result, `is`(Result.Error(exception))
        )
    }
}