package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FirebaseStringFormatTest {
    @Test
    fun firebaseSafeString() {
        val testString = "test.string#with\$all[the]characters/"
        val expectedString = "test_dot_string_hash_with_dollar_all_openbracket_the_closebracket_characters_slash_"
        assertEquals(FirebaseStringFormat.firebaseSafeString(testString), expectedString)
    }

    @Test
    fun firebaseSafeStringRevert() {
        val testString = "test_dot_string_hash_with_dollar_all_openbracket_the_closebracket_characters_slash_"
        val expectedString = "test.string#with\$all[the]characters/"
        assertEquals(FirebaseStringFormat.firebaseSafeStringRevert(testString), expectedString)
    }
}