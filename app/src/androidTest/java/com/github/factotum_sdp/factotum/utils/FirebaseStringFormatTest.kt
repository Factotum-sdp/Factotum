package com.github.factotum_sdp.factotum.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseStringFormatTest {
    @Test
    fun firebaseSafeString() {
        val testString = "test.string#with\$all[the]characters/"
        val expectedString = "test_dot_string_hash_with_dollar_all_openbracket_the_closebracket_characters_slash_"
        assert(FirebaseStringFormat.firebaseSafeString(testString) == expectedString)
    }

    @Test
    fun firebaseSafeStringRevert() {
        val testString = "test_dot_string_hash_with_dollar_all_openbracket_the_closebracket_characters_slash_"
        val expectedString = "test.string#with\$all[the]characters/"
        assert(FirebaseStringFormat.firebaseSafeStringRevert(testString) == expectedString)
    }
}