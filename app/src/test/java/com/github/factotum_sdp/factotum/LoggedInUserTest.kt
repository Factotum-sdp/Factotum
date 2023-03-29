package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoggedInUser
import com.github.factotum_sdp.factotum.data.Role
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LoggedInUserTest {

    @Test
    fun `test constructor`() {
        // Given
        val expectedDisplayName = "John Doe"
        val expectedEmail = "john.doe@gmail.com"

        // When
        val user = LoggedInUser(expectedDisplayName, expectedEmail, Role.CLIENT)

        // Then
        assertEquals(expectedDisplayName, user.displayName)
        assertEquals(expectedEmail, user.email)
    }

    @Test
    fun `test equals`() {
        // Given
        val user1 = LoggedInUser("John Doe", "john.doe@gmail.com", Role.CLIENT)
        val user2 = LoggedInUser("John Doe", "john.doe@gmail.com", Role.CLIENT)
        val user3 = LoggedInUser("Jane Smith", "jane.smith@gmail.com", Role.CLIENT)

        // Then
        assertTrue(user1 == user2)
        assertFalse(user1 == user3)
    }

    @Test
    fun `test hashCode`() {
        // Given
        val user1 = LoggedInUser("John Doe", "john.doe@gmail.com", Role.CLIENT)
        val user2 = LoggedInUser("John Doe", "john.doe@gmail.com", Role.CLIENT)
        val user3 = LoggedInUser("Jane Smith", "jane.smith@gmail.com", Role.CLIENT)

        // Then
        assertEquals(user1.hashCode(), user2.hashCode())
        assertNotEquals(user1.hashCode(), user3.hashCode())
    }

    @Test
    fun `test toString`() {
        // Given
        val expectedDisplayName = "John Doe"
        val expectedEmail = "john.doe@gmail.com"

        val user = LoggedInUser(expectedDisplayName, expectedEmail, Role.CLIENT)

        // When
        val userString = user.toString()

        // Then
        assertEquals(
            "LoggedInUser(displayName=$expectedDisplayName, email=$expectedEmail)",
            userString
        )
    }
}