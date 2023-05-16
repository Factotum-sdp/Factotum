package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.SignUpDataSink
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.models.User
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SignUpDataSinkTest {

    @Mock
    private lateinit var signUpDataSink: SignUpDataSink

    private val userName = "Jane Doe"
    private val userEmail = "jane.doe@gmail.com"
    private val validPassword = "123456"
    private val userRole = Role.BOSS
    private val username = "username"
    private val userUID = "UID"
    private val user = User(userName, userEmail, userRole, username)

    @Test
    fun signUp_Successful() {
        signUpDataSink = mock(SignUpDataSink::class.java)

        `when`(signUpDataSink.signUp(userEmail, validPassword)).thenReturn(
            Result.Success(
                userEmail
            )
        )

        // when
        val result = signUpDataSink.signUp(userEmail, validPassword)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Success(userEmail)))
    }

    @Test
    fun signUp_Failure() {
        // given
        val exception = Exception("Error signing up")

        // when
        signUpDataSink = mock(SignUpDataSink::class.java)
        `when`(signUpDataSink.signUp(userEmail, validPassword)).thenReturn(
            Result.Error(
                exception
            )
        )
        val result = signUpDataSink.signUp(userEmail, validPassword)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Error(exception)))
    }

    @Test
    fun updateUser_Successful() {
        signUpDataSink = mock(SignUpDataSink::class.java)
        `when`(signUpDataSink.updateUser(userUID, user)).thenReturn(
            Result.Success(
                userName
            )
        )
        // when
        val result = signUpDataSink.updateUser(userUID, user)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Success(userName)))
    }

    @Test
    fun updateUser_Failure() {
        // given
        val exception = Exception("Error updating user")

        // when
        signUpDataSink = mock(SignUpDataSink::class.java)
        `when`(signUpDataSink.updateUser(userUID, user)).thenReturn(
            Result.Error(
                exception
            )
        )
        val result = signUpDataSink.updateUser(userUID, user)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Error(exception)))
    }

    @Test
    fun fetchUsername_Successful() {
        signUpDataSink = mock(SignUpDataSink::class.java)
        `when`(signUpDataSink.fetchUsername(username)).thenReturn(
            Result.Success(
                username
            )
        )
        // when
        val result = signUpDataSink.fetchUsername(username)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Success(username)))
    }

    @Test
    fun fetchUsername_Failure_Username_Not_Exist() {
        // given
        val exception = Exception("Username doesn't exist")

        // when
        signUpDataSink = mock(SignUpDataSink::class.java)
        `when`(signUpDataSink.fetchUsername(username)).thenReturn(
            Result.Error(
                exception
            )
        )
        val result = signUpDataSink.fetchUsername(username)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Error(exception)))
    }

    @Test
    fun fetchUsername_Failure_Db_Connection_Impossible() {
        // given
        val exception = Exception("Connection to database impossible")

        // when
        signUpDataSink = mock(SignUpDataSink::class.java)
        `when`(signUpDataSink.fetchUsername(username)).thenReturn(
            Result.Error(
                exception
            )
        )
        val result = signUpDataSink.fetchUsername(username)

        // then
        MatcherAssert.assertThat(result, `is`(Result.Error(exception)))
    }
}