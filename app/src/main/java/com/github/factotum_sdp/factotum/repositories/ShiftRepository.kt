package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
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

/**
 * This class models a ShiftRepository. It is in charge of being the unique place where
 * to log the deliveries of a shift. It is responsible of keeping remote source up to date with
 * local source, and vice-versa.
 *
 * @property localSource : DataStore<DRecordList>. Local source where data is stored and fetched from.
 * @property shift : Shift. The shift to log deliveries for.
 * @constructor
 * Creates a ShiftRepository and sets the update of the remote and local sources.
 *
 * @param remoteSource : DatabaseReference. The remote source where data is stored and fetched from.
 */

class ShiftRepository(remoteSource: DatabaseReference,
                      private val localSource: DataStore<DRecordList>,
                      private val shift: Shift
) {

    private var backUpRef: DatabaseReference = remoteSource
    private var isConnectedToRemote = false

    private var lastShiftNetworkBackUp: DRecordList? = null
    private var lastShiftLocalNetworkBackUp: DRecordList? = null

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
        backUpRef = remoteSource
            .child(firebaseSafeString(shift.user.name))
            .child(firebaseDateFormatted(shift.date))
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

    /**
     * This method logs a delivery.
     *
     * @param record : DestinationRecord. The delivery to log.
     */
    fun logShift(deliveries : DRecordList){
        setDeliveriesBackUp(deliveries)
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