package com.github.factotum_sdp.factotum.data

import android.util.Log
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.github.factotum_sdp.factotum.utils.FirebaseStringFormat.Companion.firebaseSafeString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class DeliveryLogger {

    companion object{
        private const val DELIVERY_LOG_DB_PATH: String = "Delivery-Log"
    }
    private val dbLogRef: DatabaseReference = FirebaseDatabase.getInstance().reference
        .child(DELIVERY_LOG_DB_PATH)

    /**
     * logs the delivery of the current day
     */
    fun logDeliveries(recordsList : DRecordList, userName: String) {
        if (Firebase.auth.currentUser == null) {
            Log.w("DeliveryLogger", "No user logged in")
            return
        } else {
            recordsList.forEach { destRec ->
                if (destRec.timeStamp != null) {
                    dbLogRef
                        .child(firebaseSafeString(userName))
                        .child(dateFormatted())
                        .child(firebaseSafeString(destRec.destID)).setValue(destRec)
                }
            }
        }
    }

    private fun dateFormatted(): String {
        return SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(Date())
    }
}