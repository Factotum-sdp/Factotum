package com.github.factotum_sdp.factotum.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginRepository: LoginRepository) : BaseAuthViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    override val _authResult = MutableLiveData<BaseAuthResult<*>>()
    override val authResult: LiveData<BaseAuthResult<*>> = _authResult

    private val _retrieveProfilesResult = MutableLiveData<RetrieveProfilesResult>()
    val retrieveProfilesResult: LiveData<RetrieveProfilesResult> = _retrieveProfilesResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * Called when the login button is clicked.
     */
    override fun auth(email: String, password: String) {
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.login(email, password) }
            if (result is Result.Success) {
                _authResult.value =
                    LoginResult(
                        success = LoggedInUserView(
                            displayName = result.data.displayName,
                            email = result.data.email,
                            role = result.data.role
                        )
                    )
            } else {
                _authResult.value = LoginResult(
                    error = if ((result as Result.Error).exception.message == "User not found") {
                        R.string.user_not_found
                    } else {
                        R.string.login_failed
                    }
                )
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

    fun loginDataChanged(email: String, password: String) {
        if (!BaseAuthState.isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_email)
        } else if (!BaseAuthState.isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
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
        override val success: LoggedInUserView? = null,
        override val error: Int? = null
    ) : BaseAuthResult<LoggedInUserView>(success, error)

}