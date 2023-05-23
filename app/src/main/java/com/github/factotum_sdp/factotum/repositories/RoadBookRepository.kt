package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
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


/**
 * The RoadBook repository
 *
 * In charge of being the unique place where to fetch and push RoadBook Data, i.e
 * a DRecordList : List<DestinationRecord>.
 *
 * It resolves the conflict between a "remoteSource" and a "localSource" of data, and enable caching
 * for both remote/network requests and local requests.
 *
 * If remoteSource is not online, we rely uniquely on the localSource, and don't use the first variable cache.
 * The first variable cache is used uniquely to avoid remoteSource update.
 * However, a second variable cache is used before fetching or update the localSource.
 */
class RoadBookRepository(remoteSource: DatabaseReference, username: String,
                         private val localSource: DataStore<DRecordList>) {
    private var backUpRef: DatabaseReference
    private var isConnectedToRemote = false

    private var lastNetworkBackUp: DRecordList? = null
    private var lastLocalNetworkBackUp: DRecordList? = null

    init {
        FirebaseInstance.onConnectedStatusChanged {
            if (it) {
                CoroutineScope(Dispatchers.IO).launch {
                    // Always synchronize the network back-up when connection is back
                    setNetworkBackUp(getLastLocalBackUp())
                }
            }
            // Ensure that if first cache is set the Data is already on the remoteSource,
            // and can block unuseful additional remote updates in setBackUp()
            isConnectedToRemote = it
        }

        val date = Calendar.getInstance().time
        val dateRef = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH).format(date)
        backUpRef = remoteSource.child(dateRef) // will add a more detailed path sooner when the User data class will be stable
        initNetworkPathWithUser(username)
    }

    private fun initNetworkPathWithUser(username: String) {
        backUpRef = backUpRef.child(username)
        backUpRef.addValueEventListener(object : ValueEventListener {
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
     * Send the current records data to the the remoteSource and the localSource if connected
     * Only in the localSource otherwise
     *
     * @param records: DRecordList
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

    /**
     * Get the last available back-up
     *
     * Which is the last network written variable if the remote is connected,
     * otherwise it is fetch from the local source.
     *
     * @return the DRecordList back-up
     */
    suspend fun getLastBackUp(): DRecordList {
        if(isConnectedToRemote) {
            lastNetworkBackUp?.let {
                return it
            }
        }
        return getLastLocalBackUp()
    }

    private fun setNetworkBackUp(records: DRecordList) {
        backUpRef.setValue(records)
    }

    private suspend fun setLocalBackUp(records: DRecordList) {
        if(lastLocalNetworkBackUp != records) {
            localSource.updateData {
                lastNetworkBackUp = records
                records
            }
        }
    }

    private suspend fun getLastLocalBackUp(): DRecordList {
        return lastLocalNetworkBackUp ?: localSource.data.first()
    }

}