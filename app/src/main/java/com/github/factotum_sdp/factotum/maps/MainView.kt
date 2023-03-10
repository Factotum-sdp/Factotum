package com.github.factotum_sdp.factotum.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainView : ViewModel() {

    val routes: MutableLiveData<MutableList<Route>> = MutableLiveData(mutableListOf())

    fun addRoute(route: Route){
        routes.value?.add(route)
    }

    fun deleteAll(){
        routes.value = mutableListOf()
    }
}