package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.data.Result
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ResultTest {

    @Test
    fun `test success result`() {
        // Given
        val expectedData = "Test Data"
        val result = Result.Success(expectedData)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedData, result.data)
    }

    @Test
    fun `test error result`() {
        // Given
        val expectedException = RuntimeException("Test Exception")
        val result = Result.Error(expectedException)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(expectedException, result.exception)
    }

    @Test
    fun `test toString for success result`() {
        // Given
        val expectedData = "Test Data"
        val result = Result.Success(expectedData)

        // When
        val resultString = result.toString()

        // Then
        assertEquals("Success(data=$expectedData)", resultString)
    }

    @Test
    fun `test toString for error result`() {
        // Given
        val expectedException = RuntimeException("Test Exception")
        val result = Result.Error(expectedException)

        // When
        val resultString = result.toString()

        // Then
        assertEquals("Error(exception=$expectedException)", resultString)
    }
}