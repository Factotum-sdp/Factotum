package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.model.Result
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ResultTest {

    @Test
    fun `success result`() {
        // Given
        val expectedData = "Test Data"
        val result = Result.Success(expectedData)

        // Then
        assertEquals(expectedData, result.data)
    }

    @Test
    fun `error result`() {
        // Given
        val expectedException = RuntimeException("Test Exception")
        val result = Result.Error(expectedException)

        // Then
        assertEquals(expectedException, result.exception)
    }

    @Test
    fun `toString for success result`() {
        // Given
        val expectedData = "Test Data"
        val result = Result.Success(expectedData)

        // When
        val resultString = result.toString()

        // Then
        assertEquals("Success(data=$expectedData)", resultString)
    }

    @Test
    fun `toString for error result`() {
        // Given
        val expectedException = RuntimeException("Test Exception")
        val result = Result.Error(expectedException)

        // When
        val resultString = result.toString()

        // Then
        assertEquals("Error(exception=$expectedException)", resultString)
    }
}