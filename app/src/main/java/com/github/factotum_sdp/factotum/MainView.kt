package com.github.factotum_sdp.factotum

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainView : ViewModel() {

    val route = MutableLiveData<Route>()

    fun setRoute(_route: Route){
        route.value = Route(_route.src, _route.dst)
    }
}