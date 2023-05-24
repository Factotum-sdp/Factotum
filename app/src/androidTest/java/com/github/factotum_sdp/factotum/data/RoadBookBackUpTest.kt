package com.github.factotum_sdp.factotum.data

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.placeholder.DestinationRecords
import com.github.factotum_sdp.factotum.placeholder.UsersPlaceHolder.USER_COURIER
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookFragment
import com.github.factotum_sdp.factotum.utils.GeneralUtils
import com.github.factotum_sdp.factotum.utils.PreferencesSetting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class RoadBookBackUpTest {

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

        const val SHORT_NETWORK_DELAY = 2000L
    }
    private val courier =
        User(USER_COURIER.uid, USER_COURIER.name, USER_COURIER.email, USER_COURIER.role)

    @Before
    fun toRoadBookFragment() {
        GeneralUtils.injectLoggedInUser(testRule, courier)
        // Ensure "use RoadBook preferences" is disabled
        PreferencesSetting.setRoadBookPrefs(testRule)
        onView(ViewMatchers.withId(R.id.drawer_layout))
            .perform(DrawerActions.open())
        onView(ViewMatchers.withId(R.id.roadBookFragment))
            .perform(ViewActions.click())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun roadBookIsTimedBackedUpCorrectly() = runTest {
        val db = FirebaseInstance.getDatabase()
        val date = Calendar.getInstance().time
        val ref = db.reference
            .child(RoadBookFragment.ROADBOOK_DB_PATH)
            .child(SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH).format(date))
            .child(FirebaseStringFormat.firebaseSafeString(courier.name))

        // Edit waiting time of the second record :
        onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        onView(ViewMatchers.withId(R.id.autoCompleteClientID))
            .perform(
                ViewActions.click(),
                ViewActions.typeText("${DestinationRecords.RECORD_TO_ADD.clientID} ")
            )
        onView(ViewMatchers.withId(R.id.editTextWaitingTime))
            .perform(ViewActions.clearText(), ViewActions.typeText("7"))
        onView(ViewMatchers.withText(R.string.edit_dialog_update_b))
            .perform(ViewActions.click())

        runBlocking {
            delay(SHORT_NETWORK_DELAY)
        }

        // Set value event listener
        val future = CompletableFuture<List<DestinationRecord>>()
        ref.get().addOnSuccessListener { snapshot ->
            val records = snapshot.children.mapNotNull {
                it.getValue(DestinationRecord::class.java)
            }
            future.complete(records)

        }.addOnFailureListener {
            future.completeExceptionally(it)
        }

        val ls = future.get()
        val waitingTime = ls.last().waitingTime
        assert(waitingTime == 7)
    }
}