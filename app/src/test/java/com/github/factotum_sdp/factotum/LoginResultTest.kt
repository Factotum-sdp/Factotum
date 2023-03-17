package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.ui.login.LoggedInUserView
import com.github.factotum_sdp.factotum.ui.login.LoginResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class LoginResultTest {

    @Test
    fun `test success constructor`() {
        // Given
        val expectedUserView = LoggedInUserView("John Doe")

        // When
        val loginResult = LoginResult(success = expectedUserView)

        // Then
        assertEquals(expectedUserView, loginResult.success)
        assertEquals(null, loginResult.error)
    }

    @Test
    fun `test error constructor`() {
        // Given
        val expectedErrorCode = 401

        // When
        val loginResult = LoginResult(error = expectedErrorCode)

        // Then
        assertEquals(expectedErrorCode, loginResult.error)
        assertEquals(null, loginResult.success)
    }

    @Test
    fun `test equals`() {
        // Given
        val userView1 = LoggedInUserView("John Doe")
        val userView2 = LoggedInUserView("John Doe")
        val loginResult1 = LoginResult(success = userView1)
        val loginResult2 = LoginResult(success = userView2)
        val loginResult3 = LoginResult(error = 401)

        // Then
        assertEquals(loginResult1, loginResult2)
        assertNotEquals(loginResult1, loginResult3)
    }

    @Test
    fun `test hashCode`() {
        // Given
        val userView1 = LoggedInUserView("John Doe")
        val userView2 = LoggedInUserView("John Doe")
        val loginResult1 = LoginResult(success = userView1)
        val loginResult2 = LoginResult(success = userView2)
        val loginResult3 = LoginResult(error = 401)

        // Then
        assertEquals(loginResult1.hashCode(), loginResult2.hashCode())
        assertNotEquals(loginResult1.hashCode(), loginResult3.hashCode())
    }

    @Test
    fun `test toString`() {
        // Given
        val expectedUserView = LoggedInUserView("John Doe")
        val loginResult = LoginResult(success = expectedUserView)

        // When
        val loginResultString = loginResult.toString()

        // Then
        assertEquals("LoginResult(success=$expectedUserView, error=null)", loginResultString)
    }
}