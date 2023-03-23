package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.model.LoggedInUser
import com.github.factotum_sdp.factotum.data.Result
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.io.IOException

class LoginRepositoryTest {
    private lateinit var dataSource: LoginDataSource
    private lateinit var repository: LoginRepository

    @BeforeEach
    fun setUp() {
        dataSource = mock(LoginDataSource::class.java)
        repository = LoginRepository(dataSource)
    }

    @Test
    fun `test login success`() {
        // Given
        val username = "Jane Doe"
        val useremail = "jane.doe@gmail.com"
        val password = "123456"
        val fakeUser = LoggedInUser(username, useremail)

        `when`(dataSource.login(useremail, password)).thenReturn(Result.Success(fakeUser))

        // When
        val result = repository.login(useremail, password)

        // Then
        assertThat(result, `is`(instanceOf(Result.Success::class.java)))
        val user = (result as Result.Success).data
        assertThat(user.displayName, `is`(equalTo(username)))
        assertTrue(repository.isLoggedIn)
        assertThat(repository.user, `is`(equalTo(fakeUser)))
    }

    @Test
    fun `test login error`() {
        // Given
        val username = "testuser"
        val password = "testpassword"
        val error = IOException("Error logging in")
        `when`(dataSource.login(username, password)).thenReturn(Result.Error(error))

        // When
        val result = repository.login(username, password)

        // Then
        assertThat(result, `is`(instanceOf(Result.Error::class.java)))
        val resultError = (result as Result.Error).exception
        assertThat(resultError.message, `is`(equalTo(error.message)))
        assertThat(resultError.cause, `is`(equalTo(error.cause)))
        assertFalse(repository.isLoggedIn)
        assertNull(repository.user)
    }

}