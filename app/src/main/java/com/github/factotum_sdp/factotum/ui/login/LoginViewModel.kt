package com.github.factotum_sdp.factotum.ui.login

import androidx.core.util.PatternsCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _retrieveProfilesResult = MutableLiveData<RetrieveProfilesResult>()
    val retrieveProfilesResult: LiveData<RetrieveProfilesResult> = _retrieveProfilesResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun login(userEmail: String, password: String) {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.login(userEmail, password) }
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(
                        success = LoggedInUserView(
                            displayName = result.data.displayName,
                            email = result.data.email,
                            role = result.data.role
                        )
                    )
            } else {
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun retrieveProfiles() {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.retrieveProfiles() }
            if (result is Result.Success) {
                _retrieveProfilesResult.value =
                    RetrieveProfilesResult(
                        success = R.string.retrieve_profiles_success
                    )
            } else {
                _retrieveProfilesResult.value =
                    RetrieveProfilesResult(
                        error = R.string.retrieve_profiles_failed
                    )
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isEmailValid(username)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder email validation check
    private fun isEmailValid(username: String): Boolean {
        return if (username.contains("@")) PatternsCompat.EMAIL_ADDRESS.matcher(username)
            .matches() else false
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    /**
     * Profile retrieval result : success or error message.
     */
    data class RetrieveProfilesResult(
        val success: Int? = null,
        val error: Int? = null
    )

    /**
     * Authentication result : success (user details) or error message.
     */
    data class LoginResult(
        val success: LoggedInUserView? = null,
        val error: Int? = null
    )
}