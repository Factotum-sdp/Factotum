package com.github.factotum_sdp.factotum.ui.login

import androidx.lifecycle.*
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.repositories.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private var loginRepository: LoginRepository = LoginRepository(LoginDataSource())
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<BaseAuthResult>()
    val authResult: LiveData<BaseAuthResult> = _loginResult

    private val _retrieveUsersResult = MutableLiveData<RetrieveUserResult>()
    val retrieveUsersResult: LiveData<RetrieveUserResult> = _retrieveUsersResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun hasCachedUser(): User? {
        val user = loginRepository.getLoggedInUser()
        if (user != null) {
            _loginResult.value = LoginResult(success = user.uid)
            _retrieveUsersResult.value = RetrieveUserResult(success = user)
        }
        return user
    }

    /**
     * Called when the login button is clicked.
     */
    fun auth(email: String, password: String) {
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.login(email, password) }
            if (result is Result.Success) {
                _loginResult.value =
                    LoginResult(success = result.data)
            } else {
                _loginResult.value = LoginResult(
                    error = R.string.login_failed
                )
            }
        }
    }
    fun getCurrentUser(): User? {
        return loginRepository.getLoggedInUser()
    }

    fun logout() {
        loginRepository.logout()
    }

    fun retrieveUser(uid: String) {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.retrieveUserFromDB(uid) }
            if (result is Result.Success) {
                _retrieveUsersResult.value =
                    RetrieveUserResult(
                        success = result.data
                    )
            } else {
                _retrieveUsersResult.value =
                    RetrieveUserResult(
                        error = R.string.login_failed
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
    data class RetrieveUserResult(
        val success: User? = null,
        val error: Int? = null
    )

    /**
     * Authentication result : success (user details) or error message.
     */
    data class LoginResult(
        override val success: String? = null,
        override val error: Int? = null
    ) : BaseAuthResult(success, error)

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