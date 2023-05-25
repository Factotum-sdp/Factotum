package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
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
 * The BackUpRepository abstract class
 *
 * It implements the abstract logic to store some back-up about data given under the form of a List<Any>
 *
 * It resolves the conflict between a "remoteSource" and a "localSource" of data, and enable caching
 * for both remote/network requests and local requests.
 *
 * If remoteSource is not online, we rely uniquely on the localSource, and don't use the first variable cache.
 * The first variable cache is used uniquely to avoid remoteSource update.
 * However, a second variable cache is used before fetching or update the localSource.
 */
abstract class BackUpRepository<T: List<Any>>(remoteSource: DatabaseReference, username: String,
                                              private val localSource: DataStore<T>) {

    private var backUpRef: DatabaseReference
    private var isConnectedToRemote = false

    private var lastNetworkBackUp: T? = null
    private var lastLocalBackUp: T? = null

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

    /**
     * Extract a T instance from the DataSnapshot taken at the remoteSource/username Firebase node
     *
     * @param snapshot: DataSnapshot
     * @return T the type parameter extending List of that class
     */
    protected abstract fun extractFromSnapshot(snapshot: DataSnapshot): T

    private fun initNetworkPathWithUser(username: String) {
        backUpRef = backUpRef.child(username)
        backUpRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lastNetworkBackUp = extractFromSnapshot(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Send the current data to the the remoteSource and the localSource if connected
     * Only in the localSource otherwise
     *
     * @param data: T
     */
    open fun setBackUp(data: T) {
        if(data.isNotEmpty() && data != lastNetworkBackUp) {
            CoroutineScope(Dispatchers.Default).launch {
                if (isConnectedToRemote) {
                    setNetworkBackUp(data)
                    setLocalBackUp(data)
                } else {
                    setLocalBackUp(data)
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
     * @return T
     */
    suspend fun getLastBackUp(): T {
        if(isConnectedToRemote) {
            lastNetworkBackUp?.let {
                return it
            }
        }
        return getLastLocalBackUp()
    }

    private fun setNetworkBackUp(records: T) {
        backUpRef.setValue(records)
    }

    private suspend fun setLocalBackUp(records: T) {
        if(lastLocalBackUp != records) {
            localSource.updateData {
                lastNetworkBackUp = records
                records
            }
        }
    }

    private suspend fun getLastLocalBackUp(): T {
        return lastLocalBackUp ?: localSource.data.first()
    }
}