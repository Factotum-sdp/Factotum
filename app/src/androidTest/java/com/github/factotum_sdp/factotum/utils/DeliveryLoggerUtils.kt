package com.github.factotum_sdp.factotum.utils

import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.data.DeliveryLogger
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList

class DeliveryLoggerUtils {
    companion object{
        fun checkDeliveryLog(userName: String, dRecordList: DRecordList) {
            val dbRef = MainActivity.getDatabase().reference.child(DeliveryLogger.DELIVERY_LOG_DB_PATH)
            dbRef.get()
                .addOnSuccessListener {
                    assert(it.exists())
                }
                .addOnFailureListener {
                    assert(false)
                }
            dRecordList.forEach { destRec ->
                if (destRec.timeStamp != null) {
                    dbRef.child(FirebaseStringFormat.firebaseSafeString(userName))
                        .child(FirebaseStringFormat.firebaseDateFormatted())
                        .child(FirebaseStringFormat.firebaseSafeString(destRec.destID)).get()
                        .addOnSuccessListener {
                            assert(it.exists())
                        }
                        .addOnFailureListener {
                            assert(false)
                        }
                }
            }
        }
    }
}