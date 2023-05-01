package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.data.FirebaseInstance
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RoadBookRepository(remoteSource: DatabaseReference,
                         private val localSource: DataStore<DRecordList>) {
    private var backUpRef: DatabaseReference
    private var isConnectedToRemote = false
    private var lastNetworkBackUp: DRecordList? = null

    init {
        FirebaseInstance.onConnectedStatusChanged {
            if (it) { // if Connection is changing from no connection to connected then transfer local backup to remote
                CoroutineScope(Dispatchers.IO).launch {
                    setNetworkBackUp(fetchLocalBackUp())
                }
            }
            isConnectedToRemote = it
        }

        val date = Calendar.getInstance().time
        val dateRef = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH).format(date)
        backUpRef = remoteSource // ref path to register all back-ups from this RoadBook
                    .child(dateRef)
        //.child(getTimeInstance().format(date).plus(Random.nextInt().toString()))
        // Let uncommented for testing purpose. Uncomment it for back-up uniqueness in the DB
        // Only for demo purpose :

        backUpRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = snapshot.children.mapNotNull {
                    it.getValue(DestinationRecord::class.java)
                }
                lastNetworkBackUp = DRecordList(records)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Send the current recordsList data to the the remoteSource if connected or the localSource
     */
    fun setBackUp(records: DRecordList) {
        val allRecords = records.withArchived()
        if(allRecords.isNotEmpty() && allRecords != lastNetworkBackUp) {
            CoroutineScope(Dispatchers.Default).launch {
                if (isConnectedToRemote) {
                    setNetworkBackUp(allRecords)
                    setLocalBackUp(allRecords)
                } else {
                    setLocalBackUp(allRecords)
                }
            }
        }
    }

    suspend fun getLastBackUp(): DRecordList {
        if(isConnectedToRemote) {
            lastNetworkBackUp?.let {
                return it
            }
        }
        return fetchLocalBackUp()
    }

    private suspend fun setLocalBackUp(records: DRecordList) {
        localSource.updateData {
            records
        }
    }

    private fun setNetworkBackUp(records: DRecordList) {
        backUpRef.setValue(records)
    }

    private suspend fun fetchLocalBackUp(): DRecordList {
        return localSource.data.first()
    }

}