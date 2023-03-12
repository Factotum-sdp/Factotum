package com.github.factotum_sdp.factotum.ui.maps

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.data.Route

class MapsViewModel : ViewModel() {

    val routes: MutableLiveData<MutableList<Route>> = MutableLiveData(mutableListOf())
    val runRoute : MutableLiveData<Route> = MutableLiveData()

    fun addRoute(route: Route){
        routes.value?.add(route)
    }

    fun deleteAll(){
        routes.postValue(mutableListOf())
    }

    fun setRunRoute(route: Route){
        runRoute.value = route
        Log.d("Daniel", route.toString())
    }
}