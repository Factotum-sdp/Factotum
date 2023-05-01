package com.github.factotum_sdp.factotum.data

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.github.factotum_sdp.factotum.utils.DeliveryLoggerUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.GeneralUtils.Companion.fillUserEntryAndEnterTheApp
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeliveryLoggerTest {

    @get:Rule
    var testRule = ActivityScenarioRule(
        MainActivity::class.java
    )
    companion object {
        @BeforeClass
        @JvmStatic
        fun setUpDatabase() {
            GeneralUtils.initFirebase()
        }
    }

    val courier = UsersPlaceHolder.USER_COURIER

    @Before
    fun setUp() {
        fillUserEntryAndEnterTheApp(courier.email, courier.password)
    }

    @Test
    fun testLogDelivery() {
        val deliveryLogger = DeliveryLogger()
        val dRecordList = DRecordList(DestinationRecords.RECORDS)
        deliveryLogger.logDeliveries(dRecordList, UsersPlaceHolder.USER_COURIER.name)
        DeliveryLoggerUtils.checkDeliveryLog(UsersPlaceHolder.USER_COURIER.name, dRecordList)
    }


}