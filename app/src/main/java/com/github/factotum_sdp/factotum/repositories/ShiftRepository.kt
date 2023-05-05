package com.github.factotum_sdp.factotum.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.data.DeliveryLogger
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseDateFormatted
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseSafeString
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.models.Shift
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShiftRepository(remoteSource: DatabaseReference,
                      private val localSource: DataStore<DRecordList>,
                      private val shift: Shift
) {

    private var backUpRef: DatabaseReference = remoteSource
    private var isConnectedToRemote = false

    private var lastShiftNetworkBackUp: DRecordList? = null
    private var lastShiftLocalNetworkBackUp: DRecordList? = null

    private val deliveryLogger: DeliveryLogger = DeliveryLogger()

    init {
        FirebaseInstance.onConnectedStatusChanged {
            if(it) {
                CoroutineScope(Dispatchers.IO).launch {
                    // Always synchronize the network back-up when connection is back
                    setNetworkBackUp(getLastShiftLocalBackUp())
                }

            }
            isConnectedToRemote = it
        }
        val connectedUser = FirebaseInstance.getAuth().currentUser
        if (connectedUser?.email != shift.user.email) {
            Log.e("ShiftRepository", "Wrong user connected")
        }
        else {
            backUpRef = remoteSource
                .child(firebaseSafeString(connectedUser.displayName!!))
                .child(firebaseDateFormatted(shift.date))
        }
        backUpRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shiftsEnded = snapshot.children.mapNotNull {
                    it.getValue(DestinationRecord::class.java)
                }
                lastShiftNetworkBackUp = DRecordList(shiftsEnded)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }


    private fun setDeliveriesBackUp(records: DRecordList){
        val deliveries = DRecordList(records.filter { it.timeStamp != null })
        if (deliveries.isNotEmpty() && deliveries != lastShiftNetworkBackUp){
            CoroutineScope(Dispatchers.Default).launch {
                if (isConnectedToRemote){
                    setNetworkBackUp(deliveries)
                    setLocalBackUp(deliveries)
                } else {
                    setLocalBackUp(deliveries)
                }
            }
        }
    }

    suspend fun getLastBackUp(): DRecordList {
        if(isConnectedToRemote) {
            lastShiftNetworkBackUp?.let {
                return it
            }
        }
        return getLastShiftLocalBackUp()
    }

    fun logShift(deliveries : DRecordList){
        setDeliveriesBackUp(deliveries)
    }

    private fun setNetworkBackUp(deliveries: DRecordList) {
        backUpRef.setValue(deliveries)
    }

    private suspend fun setLocalBackUp(deliveries: DRecordList){
        if(lastShiftLocalNetworkBackUp != deliveries){
            localSource.updateData {
                lastShiftNetworkBackUp = deliveries
                deliveries
            }
        }
    }
    private suspend fun getLastShiftLocalBackUp(): DRecordList {
        return lastShiftLocalNetworkBackUp ?: localSource.data.first()
    }

}