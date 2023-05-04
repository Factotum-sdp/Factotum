package com.github.factotum_sdp.factotum.data

import android.util.Log
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseDateFormatted
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseSafeString
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase

class DeliveryLogger {

    companion object{
        const val DELIVERY_LOG_DB_PATH: String = "Delivery-Log"
    }
    private val dbLogRef: DatabaseReference = FirebaseInstance.getDatabase().reference
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
                        .child(firebaseDateFormatted())
                        .child(firebaseSafeString(destRec.destID)).setValue(destRec)
                }
            }
        }
    }
}