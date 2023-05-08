package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.Shift
import com.github.factotum_sdp.factotum.models.Shift.Companion.shiftDbPathFromRoot
import com.github.factotum_sdp.factotum.ui.roadbook.ShiftList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This class is responsible for keeping track of the shifts logged by the user.
 *
 * @property remoteSource : DatabaseReference. The remote source of the data.
 * @property localSource : DataStore<ShiftList>. The local source of the data.
 */
class ShiftRepository(private val remoteSource: DatabaseReference,
                      private val localSource: DataStore<ShiftList>) {

    companion object{
        const val DELIVERY_LOG_DB_PATH: String = "Delivery-Log"
    }

    private var backUpRef: DatabaseReference = remoteSource
    private var isConnectedToRemote = false

    private var localShiftList: ShiftList = ShiftList(emptyList())

    init {
        FirebaseInstance.onConnectedStatusChanged {
            if(it) {
                CoroutineScope(Dispatchers.IO).launch {
                    // Always synchronize the network back-up when connection is back
                    setNetworkShiftList()
                }

            }
            isConnectedToRemote = it
        }


    }


    /**
     * This methog logs a shift to the local and remote sources.
     *
     * @param shift : Shift. The shift to log.
     */
    fun logShift(shift : Shift){
        if(!localShiftList.contains(shift))
            localShiftList = localShiftList.add(shift)
        CoroutineScope(Dispatchers.Default).launch {
            addToLocalSource()
        }
        if(isConnectedToRemote){
            setNetworkShiftList()
        }
    }

    private suspend fun addToLocalSource(){
        localSource.updateData {
            localShiftList
        }
    }

    private fun setNetworkShiftList(){
        val mapUserNbShift = mutableMapOf<String, Int>()
        localShiftList.forEach { shift ->
            mapUserNbShift[shift.user.name] = (mapUserNbShift[shift.user.name] ?: 0) + 1
            val nbShift = mapUserNbShift[shift.user.name]!!
            shiftDbPathFromRoot(backUpRef, shift)
                .child(nbShift.toString())
                .setValue(shift)}
    }

}