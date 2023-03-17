package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.ui.login.LoggedInUserView
import org.junit.Assert.assertNotEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoggedInUserViewTest {

    @Test
    fun `test constructor`() {
        // Given
        val expectedDisplayName = "John Doe"

        // When
        val userView = LoggedInUserView(expectedDisplayName)

        // Then
        assertEquals(expectedDisplayName, userView.displayName)
    }

    @Test
    fun `test equals`() {
        // Given
        val userView1 = LoggedInUserView("John Doe")
        val userView2 = LoggedInUserView("John Doe")
        val userView3 = LoggedInUserView("Jane Smith")

        // Then
        assertEquals(userView1, userView2)
        assertNotEquals(userView1, userView3)
    }

    @Test
    fun `test hashCode`() {
        // Given
        val userView1 = LoggedInUserView("John Doe")
        val userView2 = LoggedInUserView("John Doe")
        val userView3 = LoggedInUserView("Jane Smith")

        // Then
        assertEquals(userView1.hashCode(), userView2.hashCode())
        assertNotEquals(userView1.hashCode(), userView3.hashCode())
    }

    @Test
    fun `test toString`() {
        // Given
        val expectedDisplayName = "John Doe"
        val userView = LoggedInUserView(expectedDisplayName)

        // When
        val userViewString = userView.toString()

        // Then
        assertEquals("LoggedInUserView(displayName=$expectedDisplayName)", userViewString)
    }
}