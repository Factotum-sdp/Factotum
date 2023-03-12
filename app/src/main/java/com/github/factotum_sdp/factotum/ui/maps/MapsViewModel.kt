package com.github.factotum_sdp.factotum.ui.maps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.data.Route

/**
 * viewModel of the routes and map fragments
 *
 */
class MapsViewModel : ViewModel() {

    val routes: MutableLiveData<MutableList<Route>> = MutableLiveData(mutableListOf())
    val runRoute : MutableLiveData<Route> = MutableLiveData()

    /**
     * Adds a route to be shown on the map
     *
     * @param route : route to be shown
     */
    fun addRoute(route: Route){
        routes.value?.add(route)
    }

    /**
     * deletes all the routes
     *
     */
    fun deleteAll(){
        routes.postValue(mutableListOf())
    }

    /**
     * Sets the route to be run
     *
     * @param route : route to be run
     */
    fun setRunRoute(route: Route){
        runRoute.postValue(route)
    }
}