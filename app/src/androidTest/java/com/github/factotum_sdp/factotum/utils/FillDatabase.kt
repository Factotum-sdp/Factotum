package com.github.factotum_sdp.factotum.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FillDatabase {

    @Test
    fun setUpDatabase() {
        ContactsUtils.emptyFirebaseDatabase()

        runBlocking {
            ContactsUtils.populateDatabase(5)
        }
    }

}