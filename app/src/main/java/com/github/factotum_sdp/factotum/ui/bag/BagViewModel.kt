package com.github.factotum_sdp.factotum.ui.bag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.models.Package

class BagViewModel: ViewModel() {
    private val _packages = MutableLiveData<List<Package>>()
    val packages: LiveData<List<Package>> = _packages

}