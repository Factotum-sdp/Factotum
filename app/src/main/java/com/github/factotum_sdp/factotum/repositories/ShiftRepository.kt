package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseDateFormatted
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseSafeString
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseTimeFormatted
import com.github.factotum_sdp.factotum.models.Shift
import com.github.factotum_sdp.factotum.ui.roadbook.ShiftList
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


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


    init {
        FirebaseInstance.onConnectedStatusChanged {
            if(it) {
                CoroutineScope(Dispatchers.IO).launch {
                    // Always synchronize the network back-up when connection is back
                    setNetworkShiftList(getFromLocalSource())
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
        var localShiftList = runBlocking { getFromLocalSource() }
        if(!localShiftList.contains(shift))
            localShiftList = localShiftList.add(shift)
        CoroutineScope(Dispatchers.Default).launch {
            addToLocalSource(localShiftList)
        }
        if(isConnectedToRemote){
            setNetworkShiftList(localShiftList)
        }
    }

    private suspend fun addToLocalSource(localShiftList: ShiftList){
        localSource.updateData {
            localShiftList
        }
    }

    private fun setNetworkShiftList(localShiftList: ShiftList){
        localShiftList.forEach { shift ->
            shiftDbPathFromRoot(shift)
                .setValue(shift)
        }
    }

    private suspend fun getFromLocalSource(): ShiftList{
        return try{ localSource.data.first() }
        catch (e: Exception){
            ShiftList(emptyList())
        }
    }

    private fun shiftDbPathFromRoot(shift: Shift): DatabaseReference {
        return backUpRef.child(firebaseSafeString(shift.user.name))
            .child(firebaseDateFormatted(shift.date))
            .child(firebaseTimeFormatted(shift.date))
    }

}