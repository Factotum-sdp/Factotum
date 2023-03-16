package com.github.factotum_sdp.factotum

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * ViewModel class for the App User
 * Observed in MainActivity
 */
class UserViewModel(userValue: String, nameValue: String) : ViewModel() {
    val email: MutableLiveData<String> = MutableLiveData()
    val name: MutableLiveData<String> = MutableLiveData()
    init {
        email.value = userValue
        name.value = nameValue
    }

    // Factory needed to assign values at construction time to the class attributes
    class UserViewModelFactory(private val nameValue: String, private val emailValue: String)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                        .getConstructor(String::class.java, String::class.java)
                        .newInstance(nameValue, emailValue)
        }
    }
}
