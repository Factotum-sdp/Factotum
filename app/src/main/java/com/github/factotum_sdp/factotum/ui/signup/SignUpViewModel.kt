package com.github.factotum_sdp.factotum.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.SignUpDataSink
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class SignUpViewModel(
    private val signUpDataSink: SignUpDataSink
) : ViewModel() {

    private val _signupForm = MutableLiveData<SignUpFormState>()
    val signupFormState: LiveData<SignUpFormState> = _signupForm

    private val _signupResult = MutableLiveData<BaseAuthResult>()
    val authResult: LiveData<BaseAuthResult> = _signupResult

    private val _updateUserResult = MutableLiveData<UpdateUsersResult>()
    val updateUserResult: LiveData<UpdateUsersResult> = _updateUserResult

    private val _fetchUsernameResult = MutableLiveData<FetchUsernameResult>()
    val fetchUsernameResult: LiveData<FetchUsernameResult> = _fetchUsernameResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * Called when the sign up button is clicked.
     */
    fun auth(email: String, password: String) {
        viewModelScope.launch {
            val result = withContext(dispatcher) { signUpDataSink.signUp(email, password) }
            if (result is Result.Success) {
                _signupResult.value =
                    SignUpResult(
                        success = result.data
                    )
            } else {
                _signupResult.value = SignUpResult(
                    error = R.string.signup_failed
                )
            }
        }
    }

    fun updateUser(userUID: String, user: User) {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { signUpDataSink.updateUser(userUID, user) }
            if (result is Result.Success) {
                _updateUserResult.value =
                    UpdateUsersResult(
                        success = result.data
                    )
            } else {
                _updateUserResult.value = UpdateUsersResult(
                    error = R.string.update_users_failed
                )
            }
        }
    }

    fun fetchUsername(username: String) {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { signUpDataSink.fetchUsername(username) }
            if (result is Result.Success) {
                _fetchUsernameResult.value = FetchUsernameResult(
                    success = result.data
                )
            } else if (result is Result.Error &&
                result.exception is IOException &&
                result.exception.message == "Username doesn't exist"
            ) {
                _fetchUsernameResult.value = FetchUsernameResult(
                    error = R.string.user_not_found
                )
            } else {
                _fetchUsernameResult.value = FetchUsernameResult(
                    error = R.string.database_error
                )
            }

        }
    }

    fun signUpDataChanged(
        name: String,
        email: String,
        password: String,
        role: String,
        username: String
    ) {
        if (!isNameValid(name)) {
            _signupForm.value = SignUpFormState(nameError = R.string.invalid_name)
        } else if (!BaseAuthState.isEmailValid(email)) {
            _signupForm.value = SignUpFormState(emailError = R.string.invalid_email)
        } else if (!BaseAuthState.isPasswordValid(password)) {
            _signupForm.value = SignUpFormState(passwordError = R.string.invalid_password)
        } else if (role.isBlank()) {
            //
        } else if (username.isBlank()) {
            _signupForm.value = SignUpFormState(usernameError = R.string.empty_username_error)
        } else {
            _signupForm.value = SignUpFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    /**
     * Fetch client ID result : success or error message.
     */
    data class FetchUsernameResult(
        val success: String? = null,
        val error: Int? = null
    )

    /**
     * Profile update result : success or error message.
     */
    data class UpdateUsersResult(
        val success: String? = null,
        val error: Int? = null
    )

    /**
     * Sign up result : success (new user name) or error message.
     */
    data class SignUpResult(
        override val success: String? = null,
        override val error: Int? = null
    ) : BaseAuthResult(success, error)
}