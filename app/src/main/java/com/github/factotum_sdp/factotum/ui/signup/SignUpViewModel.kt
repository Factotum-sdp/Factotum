package com.github.factotum_sdp.factotum.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.SignUpDataSink
import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel(
    private val signUpDataSink: SignUpDataSink,
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _signupForm = MutableLiveData<SignUpFormState>()
    val signupFormState: LiveData<SignUpFormState> = _signupForm

    private val _signupResult = MutableLiveData<BaseAuthResult>()
    val authResult: LiveData<BaseAuthResult> = _signupResult

    private val _updateUserResult = MutableLiveData<UpdateUsersResult>()
    val updateUserResult: LiveData<UpdateUsersResult> = _updateUserResult

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
                _signupResult.value = SignUpResult(error = R.string.signup_failed)
            }
        }
    }

    fun updateUser(userUID: String, user: User) {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { signUpDataSink.updateUsersList(userUID, user) }
            if (result is Result.Success) {
                _updateUserResult.value =
                    UpdateUsersResult(
                        success = result.data
                    )
            } else {
                _updateUserResult.value = UpdateUsersResult(error = R.string.update_users_failed)
            }
        }
    }

    fun signUpDataChanged(
        username: String,
        email: String,
        password: String,
        role: String,
        clientId: String
    ) {
        if (!isUserNameValid(username)) {
            _signupForm.value = SignUpFormState(usernameError = R.string.invalid_username)
        } else if (!BaseAuthState.isEmailValid(email)) {
            _signupForm.value = SignUpFormState(emailError = R.string.invalid_email)
        } else if (!BaseAuthState.isPasswordValid(password)) {
            _signupForm.value = SignUpFormState(passwordError = R.string.invalid_password)
        } else if (role.isBlank()) {
            //
        } else if (!isClientIdValid(clientId)) {
            //
        } else {
            _signupForm.value = SignUpFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isClientIdValid(clientId: String): Boolean {
        val dbRef = MainActivity.getDatabase().reference
        var isClientIdValid = true
        if (clientId.isBlank()) {
            _signupForm.value = SignUpFormState(clientIdError = R.string.empty_clientId_error)
            return false
        }
        dbRef.child("contacts-bis")
            .child(clientId)
            .get().addOnSuccessListener {
                if (it.exists()) {
                    isClientIdValid = false
                    _signupForm.value = SignUpFormState(clientIdError = R.string.invalid_clientId)
                }
            }.addOnFailureListener {
                isClientIdValid = false
                _signupForm.value = SignUpFormState(clientIdError = R.string.database_error)
            }
        return isClientIdValid
    }

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