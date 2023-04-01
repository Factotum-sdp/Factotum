package com.github.factotum_sdp.factotum.ui.signup

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.R

class SignUpViewModel : ViewModel() {

    private val _signupForm = MutableLiveData<SignUpFormState>()
    val signupFormState: LiveData<SignUpFormState> = _signupForm

    fun signUpDataChanged(username: String, email: String, password: String, role: String) {
        if (!isUserNameValid(username)) {
            _signupForm.value = SignUpFormState(usernameError = R.string.invalid_username)
        } else if (!isEmailValid(email)) {
            _signupForm.value = SignUpFormState(emailError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _signupForm.value = SignUpFormState(passwordError = R.string.invalid_password)
        } else if (role.isBlank()) {
            _signupForm.value = SignUpFormState(roleError = R.string.no_role_selected)
        } else {
            _signupForm.value = SignUpFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }
    
    // A placeholder email validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains("@")) PatternsCompat.EMAIL_ADDRESS.matcher(email)
            .matches() else false
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

}