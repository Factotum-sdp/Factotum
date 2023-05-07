package com.github.factotum_sdp.factotum.ui.bossmap

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.CourierLocation
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

private const val WAIT_TIME_LOCATION_UPDATE = 15000L

class BossMapViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseInstance.getDatabase().reference.child("Location")
    private val handler = Handler(Looper.getMainLooper())

    private val _courierLocations = MutableLiveData<List<CourierLocation>>()
    val courierLocations: LiveData<List<CourierLocation>> get() = _courierLocations

    private val fetchDataRunnable = object : Runnable {
        override fun run() {
            fetchLocationsAndUpdateMarkers()
            handler.postDelayed(this, WAIT_TIME_LOCATION_UPDATE)
        }
    }

    init {
        handler.post(fetchDataRunnable)
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

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(fetchDataRunnable)
    }
}