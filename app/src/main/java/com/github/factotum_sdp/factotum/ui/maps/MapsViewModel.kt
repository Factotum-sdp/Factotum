package com.github.factotum_sdp.factotum.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.models.Location
import com.github.factotum_sdp.factotum.models.Route

/**
 * viewModel of the routes and map fragments
 *
 */
class MapsViewModel : ViewModel() {

    private val _routes: MutableLiveData<List<Route>> = MutableLiveData(listOf())
    private val _runRoute: MutableLiveData<Route> = MutableLiveData()
    private val _location: MutableLiveData<Location> = MutableLiveData()
    val routesState: LiveData<List<Route>> = _routes
    val runRouteState: LiveData<Route> = _runRoute
    val location: LiveData<Location> = _location

    /**
     * Adds a route to be shown on the map
     *
     * @param route : route to be shown
     */
    fun addRoute(route: Route) {
        _routes.value?.let {
            val res = it.plus(route)
            _routes.postValue(res)
        }
    }

    /**
     * Add a list of route to be shown on the map
     *
     * @param routes : routes to be shown
     */
    fun addAll(routes: List<Route>) {
        _routes.value?.let {
            val res = it.plus(routes)
            _routes.postValue(res)
        }
    }

    /**
     * deletes all the routes
     *
     */
    fun deleteAll() {
        _routes.postValue(listOf())
    }

    /**
     * Sets the route to be run
     *
     * @param route : route to be run
     */
    fun setRunRoute(route: Route) {
        _runRoute.postValue(route)
    }

    /**
     * Sets the location
     *
     * @param query : String. Address to search location
     * @param context : Context. Context in which this function is called
     * @return returns the created location
     */
    fun setLocation(location: Location) {
        _location.value = location
    }
}