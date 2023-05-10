package com.github.factotum_sdp.factotum

import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date
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

    @Test
    fun firebaseDateRevert(){
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(2016, 7, 10, 0, 0, 0)
        val date = Date(calendar.timeInMillis)
        val formattedDate = FirebaseStringFormat.firebaseDateFormatted(date)
        val revertedDate = FirebaseStringFormat.firebaseParseDate(formattedDate)
        assertEquals(date, revertedDate)
    }

    @Test
    fun firebaseHourRevert(){
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.HOUR, 10)
        calendar.set(Calendar.MINUTE, 10)
        val date = Date(calendar.timeInMillis)
        val formattedDate = FirebaseStringFormat.firebaseTimeFormatted(date)
        val revertedDate = FirebaseStringFormat.firebaseParseTime(formattedDate)
        assertEquals(date.time, revertedDate!!.time)
    }
}