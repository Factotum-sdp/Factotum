package com.github.factotum_sdp.factotum.ui.login

import androidx.lifecycle.*
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.data.*
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val userViewModel: UserViewModel) : BaseAuthViewModel() {

    private var loginRepository: LoginRepository = LoginRepository(LoginDataSource())
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<BaseAuthResult<*>>()
    override val authResult: LiveData<BaseAuthResult<*>> = _loginResult

    private val _retrieveUsersResult = MutableLiveData<RetrieveUsersResult>()
    val retrieveUsersResult: LiveData<RetrieveUsersResult> = _retrieveUsersResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * Called when the login button is clicked.
     */
    override fun auth(email: String, password: String) {
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.login(email, password) }
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

    fun retrieveUsersList() {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.retrieveUsersList() }
            if (result is Result.Success) {
                _retrieveUsersResult.value =
                    RetrieveUsersResult(
                        success = R.string.retrieve_users_success
                    )
            } else {
                _retrieveUsersResult.value =
                    RetrieveUsersResult(
                        error = R.string.retrieve_users_failed
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
    data class RetrieveUsersResult(
        val success: Int? = null,
        val error: Int? = null
    )

    /**
     * Authentication result : success (user details) or error message.
     */
    data class LoginResult(
        override val success: User? = null,
        override val error: Int? = null
    ) : BaseAuthResult<User>(success, error)

    // Factory needed to assign a value at construction time to the class attribute
    class LoginViewModelFactory(private val userViewModel: UserViewModel) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(UserViewModel::class.java)
                .newInstance(userViewModel)
        }
    }

}