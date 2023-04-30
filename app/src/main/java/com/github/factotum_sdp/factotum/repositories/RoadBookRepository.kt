package com.github.factotum_sdp.factotum.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.data.FirebaseInstance
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "ROADBOOK_REPOSITORY"

class RoadBookRepository(private val remoteSource: DatabaseReference,
                         private val localSource: DataStore<DRecordList>) {
    private var isConnectedToRemote = false
    private var backUpRef: DatabaseReference

    init {
        FirebaseInstance.onConnectedStatusChanged {
            if (it) { // if Connection is changing from no connection to connected then transfer local backup to remote
                CoroutineScope(Dispatchers.IO).launch {
                    networkBackUp(fetchLocalBackUp())
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
    }

    /**
     * Send the current recordsList data to the Database referenced at construction time
     */
    fun backUp(records: DRecordList) {
        CoroutineScope(Dispatchers.Default).launch {
            if(isConnectedToRemote) {
                networkBackUp(records)
            } else {
                localBackUp(records)
            }
        }
    }

    private suspend fun localBackUp(records: DRecordList) {
        localSource.updateData {
            records
        }
    }

    private fun networkBackUp(records: DRecordList) {
        backUpRef.setValue(records)
    }

    private suspend fun fetchLocalBackUp(): DRecordList {
        return readLocalBackUp.last()
    }

    private val readLocalBackUp: Flow<DRecordList> =
        localSource.data.catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e(TAG, "Error reading RoadBook local backup.", exception)
                emit(DRecordList())
            } else {
                throw exception
            }
        }

    /*
    private suspend fun fetchRemoteBackUp(): DRecordList {}

    // For repository API
    fun fetchBackUp(): DRecordList{}
     */
}