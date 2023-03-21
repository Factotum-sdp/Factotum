package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.LoggedInUser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class LoggedInUserTest {

    @Test
    fun `test constructor`() {
        // Given
        val expectedUserId = "1234"
        val expectedDisplayName = "John Doe"

        // When
        val user = LoggedInUser(expectedUserId, expectedDisplayName)

        // Then
        assertEquals(expectedUserId, user.userId)
        assertEquals(expectedDisplayName, user.displayName)
    }

    @Test
    fun `test equals`() {
        // Given
        val user1 = LoggedInUser("1234", "John Doe")
        val user2 = LoggedInUser("1234", "John Doe")
        val user3 = LoggedInUser("5678", "Jane Smith")

        // Then
        assertTrue(user1 == user2)
        assertFalse(user1 == user3)
    }

    @Test
    fun `test hashCode`() {
        // Given
        val user1 = LoggedInUser("1234", "John Doe")
        val user2 = LoggedInUser("1234", "John Doe")
        val user3 = LoggedInUser("5678", "Jane Smith")

        // Then
        assertEquals(user1.hashCode(), user2.hashCode())
        assertNotEquals(user1.hashCode(), user3.hashCode())
    }

    @Test
    fun `test toString`() {
        // Given
        val expectedUserId = "1234"
        val expectedDisplayName = "John Doe"
        val user = LoggedInUser(expectedUserId, expectedDisplayName)

        // When
        val userString = user.toString()

        // Then
        assertEquals("LoggedInUser(userId=$expectedUserId, displayName=$expectedDisplayName)", userString)
    }
}