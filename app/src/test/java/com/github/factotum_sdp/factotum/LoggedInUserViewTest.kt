package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.ui.login.LoggedInUserView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class LoggedInUserViewTest {

    @Test
    fun `constructor is correct`() {
        // Given
        val expectedDisplayName = "John Doe"
        val expectedEmail = "john.doe@gmail.com"

        // When
        val userView = LoggedInUserView(expectedDisplayName, expectedEmail)

        // Then
        assertEquals(expectedDisplayName, userView.displayName)
    }

    @Test
    fun `equals is correct`() {
        // Given
        val userView1 = LoggedInUserView("John Doe", "john.doe@gmail.com")
        val userView2 = LoggedInUserView("John Doe", "john.doe@gmail.com")
        val userView3 = LoggedInUserView("Jane Smith", "jane.smith@gmail.com")

        // Then
        assertEquals(userView1, userView2)
        assertNotEquals(userView1, userView3)
    }

    @Test
    fun `hashCode is correct`() {
        // Given
        val userView1 = LoggedInUserView("John Doe", "john.doe@gmail.com")
        val userView2 = LoggedInUserView("John Doe", "john.doe@gmail.com")
        val userView3 = LoggedInUserView("Jane Smith", "jane.smith@gmail.com")

        // Then
        assertEquals(userView1.hashCode(), userView2.hashCode())
        assertNotEquals(userView1.hashCode(), userView3.hashCode())
    }

    @Test
    fun `toString is correct`() {
        // Given
        val expectedDisplayName = "John Doe"
        val expectedEmail = "john.doe@gmail.com"
        val userView = LoggedInUserView(expectedDisplayName, expectedEmail)

        // When
        val userViewString = userView.toString()

        // Then
        assertEquals(
            "LoggedInUserView(displayName=$expectedDisplayName, email=$expectedEmail)",
            userViewString
        )
    }
}