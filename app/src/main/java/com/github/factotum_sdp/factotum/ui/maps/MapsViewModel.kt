package com.github.factotum_sdp.factotum.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.data.Route

class MapsViewModel : ViewModel() {

    val routes: MutableLiveData<MutableList<Route>> = MutableLiveData(mutableListOf())

    fun addRoute(route: Route){
        routes.value?.add(route)
    }

    fun deleteAll(){
        routes.postValue(mutableListOf())
    }
}