package com.github.factotum_sdp.factotum.ui.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.model.Result
import com.github.factotum_sdp.factotum.model.User
import com.github.factotum_sdp.factotum.repositories.LoginRepository
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel (context: Context): ViewModel() {

    private var loginRepository: LoginRepository = LoginRepository(LoginDataSource(), context)
    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<BaseAuthResult>()
    val authResult: LiveData<BaseAuthResult> = _loginResult

    private val _retrieveUsersResult = MutableLiveData<RetrieveUserResult>()
    val retrieveUsersResult: LiveData<RetrieveUserResult> = _retrieveUsersResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    fun checkIfCachedUser(): User? {
        val user = loginRepository.getLoggedInUser()
        if (user != null) {
            _loginResult.value = LoginResult(success = user.uid)
            _retrieveUsersResult.value = RetrieveUserResult(success = user)
            return user

        }
        return null
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
    class LoginViewModelFactory(private val context: Context) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass
                .getConstructor(Context::class.java)
                .newInstance(context)
        }
    }

}