package com.github.factotum_sdp.factotum.ui.login

import androidx.lifecycle.*
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val userViewModel: UserViewModel) : ViewModel() {

    private var loginRepository: LoginRepository = LoginRepository(LoginDataSource())
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
                userViewModel.setLoggedInUser(result.data)
                _loginResult.value =
                    LoginResult(success = result.data)
            } else {
                _loginResult.value = LoginResult(
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
        val success: User? = null,
        val error: Int? = null
    )

    // Factory needed to assign a value at construction time to the class attribute
    class LoginViewModelFactory(private val userViewModel: UserViewModel)
        : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(UserViewModel::class.java)
                .newInstance(userViewModel)
        }
    }

}