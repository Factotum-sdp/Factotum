package com.github.factotum_sdp.factotum.ui.signup

import androidx.lifecycle.*
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.LoginRepository
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.data.SignUpDataSink
import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignUpViewModel(
    private val signUpDataSink: SignUpDataSink,
    private val loginRepository: LoginRepository
) : BaseAuthViewModel() {

    private val _signupForm = MutableLiveData<SignUpFormState>()
    val signupFormState: LiveData<SignUpFormState> = _signupForm

    private val _signupResult = MutableLiveData<BaseAuthResult<*>>()
    override val authResult: LiveData<BaseAuthResult<*>> = _signupResult

    private val _updateUsersResult = MutableLiveData<UpdateUsersResult>()
    val updateUsersResult: LiveData<UpdateUsersResult> = _updateUsersResult

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    /**
     * Called when the sign up button is clicked.
     */
    override fun auth(email: String, password: String) {
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

    fun updateUsersList(user: User) {
        // launch in a separate asynchronous job
        viewModelScope.launch {
            val result = withContext(dispatcher) { loginRepository.updateUserList(user) }
            if (result is Result.Success) {
                _updateUsersResult.value =
                    UpdateUsersResult(
                        success = result.data
                    )
            } else {
                _updateUsersResult.value = UpdateUsersResult(error = R.string.update_users_failed)
            }
        }
    }

    fun signUpDataChanged(username: String, email: String, password: String, role: String) {
        if (!isUserNameValid(username)) {
            _signupForm.value = SignUpFormState(usernameError = R.string.invalid_username)
        } else if (!BaseAuthState.isEmailValid(email)) {
            _signupForm.value = SignUpFormState(emailError = R.string.invalid_email)
        } else if (!BaseAuthState.isPasswordValid(password)) {
            _signupForm.value = SignUpFormState(passwordError = R.string.invalid_password)
        } else if (role.isBlank()) {
            //
        } else {
            _signupForm.value = SignUpFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank()
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
    ) : BaseAuthResult<String>(success, error)
}