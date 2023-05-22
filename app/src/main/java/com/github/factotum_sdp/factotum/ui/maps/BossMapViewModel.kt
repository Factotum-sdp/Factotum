package com.github.factotum_sdp.factotum.ui.maps

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.Contact
import com.github.factotum_sdp.factotum.models.CourierLocation
import com.github.factotum_sdp.factotum.models.DeliveryStatus
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.ui.roadbook.RoadBookFragment.Companion.ROADBOOK_DB_PATH
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val WAIT_TIME_LOCATION_UPDATE = 15000L

class BossMapViewModel : ViewModel() {

    private val database: DatabaseReference = FirebaseInstance.getDatabase().reference.child("Location")
    private val roadbookDbRef : DatabaseReference = FirebaseInstance.getDatabase().reference.child(ROADBOOK_DB_PATH)
    private val handler = Handler(Looper.getMainLooper())

    private val _courierLocations = MutableLiveData<List<CourierLocation>>()
    private val _deliveriesStatus = MutableLiveData<Map<String,List<DeliveryStatus>>>()
    private val _contacts = MutableLiveData<List<Contact>>()

    val courierLocations: LiveData<List<CourierLocation>> get() = _courierLocations
    val deliveriesStatus : LiveData<Map<String,List<DeliveryStatus>>> get() = _deliveriesStatus


    private val fetchDataRunnable = object : Runnable {
        override fun run() {
            fetchLocationsAndUpdateMarkers()
            handler.postDelayed(this, WAIT_TIME_LOCATION_UPDATE)
        }
    }

    init {
        handler.post(fetchDataRunnable)
        fetchDeliveryState()
    }

        private fun fetchLocationsAndUpdateMarkers() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = snapshot.children.mapNotNull { child ->
                    val uid = child.key ?: "Unknown"
                    val name = child.child("name").getValue(String::class.java) ?: "Unknown"
                    val latitude = child.child("latitude").getValue(Double::class.java) ?: 0.0
                    val longitude = child.child("longitude").getValue(Double::class.java) ?: 0.0

                    CourierLocation(uid, name, latitude, longitude)
                }
                _courierLocations.value = locations
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("BossMapViewModel: ", "onCancelled: $error")
            }
        })
    }

    private fun fetchDeliveryState(){
        roadbookDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val mapCourierDeliveryStatus = mutableMapOf<String,MutableList<DeliveryStatus>>()
                val date = Calendar.getInstance().time
                val dateRef = SimpleDateFormat.getDateInstance(DateFormat.DEFAULT, Locale.ENGLISH).format(date)
                snapshot.child(dateRef).children.forEach { user ->
                    user.key?.let { username ->
                        user.children.forEach {
                            val record = it.getValue(DestinationRecord::class.java)
                            val client = _contacts.value?.find { contact -> contact.username == record?.clientID }
                            if (client?.latitude != null && client.longitude != null) {
                                val dStatus = DeliveryStatus(
                                    courier = username,
                                    destID = record?.destID ?: "",
                                    clientID = record?.clientID ?: "",
                                    timeStamp = record?.timeStamp,
                                    addressName = client.addressName,
                                    latitude = client.latitude,
                                    longitude = client.longitude
                                )
                                mapCourierDeliveryStatus[client.username]?.add(dStatus) ?: run {
                                    mapCourierDeliveryStatus[client.username] = mutableListOf(dStatus)
                                }
                            }
                        }
                    } ?: Log.e("BossMapViewModel: ", "onDataChange: user.key is null")
                }
                _deliveriesStatus.value = mapCourierDeliveryStatus
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("BossMapViewModel: ", "onCancelled: $error")
            }
        })
    }

    fun updateContacts(updateContacts : List<Contact>){
        _contacts.value = updateContacts
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(fetchDataRunnable)
    }
}