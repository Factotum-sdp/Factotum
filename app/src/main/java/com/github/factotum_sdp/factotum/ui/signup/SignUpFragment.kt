package com.github.factotum_sdp.factotum.ui.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.*
import com.github.factotum_sdp.factotum.databinding.FragmentSignupBinding
import com.github.factotum_sdp.factotum.models.Role
import com.github.factotum_sdp.factotum.models.User
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private var _binding: FragmentSignupBinding? = null

    private val roles = listOf("BOSS", "COURIER", "CLIENT")
    private lateinit var adapter: ArrayAdapter<String>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, SignUpViewModelFactory())[SignUpViewModel::class.java]

        val usernameEditText = binding.username
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val roleAutoCompleteTextView = binding.role
        val clientIdEditText = binding.clientId
        val loadingProgressBar = binding.loading
        val signUpButton = binding.signup

        observeSignUpFormState(
            signUpButton,
            usernameEditText,
            emailEditText,
            passwordEditText,
            clientIdEditText
        )

        val afterTextChangedListener = createTextWatcher(
            viewModel,
            usernameEditText,
            emailEditText,
            passwordEditText,
            roleAutoCompleteTextView,
            clientIdEditText
        )

        addListeners(
            usernameEditText,
            emailEditText,
            passwordEditText,
            roleAutoCompleteTextView,
            clientIdEditText,
            afterTextChangedListener,
        )

        listenToAuthButton(
            signUpButton,
            loadingProgressBar
        )

        observeAuthResult(loadingProgressBar)

        adapter = ArrayAdapter(requireContext(), R.layout.user_role_item, roles)

        roleAutoCompleteTextView.setAdapter(adapter)
    }

    private fun observeSignUpFormState(
        signupButton: Button,
        usernameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        clientIdEditText: EditText
    ) {
        viewModel.signupFormState.observe(viewLifecycleOwner,
            Observer { signupFormState ->
                if (signupFormState == null) {
                    return@Observer
                }
                signupButton.isEnabled = signupFormState.isDataValid
                signupFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                signupFormState.emailError?.let {
                    emailEditText.error = getString(it)
                }
                signupFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
                signupFormState.clientIdError?.let {
                    clientIdEditText.error = getString(it)
                }
            })
    }

    private fun createTextWatcher(
        viewModel: SignUpViewModel,
        usernameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        roleAutoCompleteTextView: EditText,
        clientIdEditText: EditText
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.signUpDataChanged(
                    usernameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    roleAutoCompleteTextView.text.toString(),
                    clientIdEditText.text.toString()
                )
            }
        }
    }

    private fun addListeners(
        usernameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        roleAutoCompleteTextView: EditText,
        clientIdEditText: EditText,
        afterTextChangedListener: TextWatcher
    ) {
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        clientIdEditText.addTextChangedListener(afterTextChangedListener)
        roleAutoCompleteTextView.addTextChangedListener(afterTextChangedListener)
    }

    private fun listenToAuthButton(
        authButton: Button,
        loadingProgressBar: ProgressBar
    ) {
        authButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            viewModel.fetchClientId(binding.clientId.text.toString())
        }
    }

    private fun observeAuthResult(loadingProgressBar: View) {

        viewModel.fetchClientIdResult.observe(viewLifecycleOwner,
            Observer { fetchClientIdResult ->
                fetchClientIdResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                fetchClientIdResult.error?.let {
                    showSignUpFailed(it)
                }
                fetchClientIdResult.success?.let {
                    viewModel.auth(
                        binding.email.text.toString(),
                        binding.password.text.toString()
                    )
                }
            })

        viewModel.authResult.observe(viewLifecycleOwner,
            Observer { authResult ->
                authResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                authResult.error?.let {
                    showSignUpFailed(it)
                }
                authResult.success?.let {
                    val newUser = User(
                        binding.username.text.toString(),
                        binding.email.text.toString(),
                        Role.valueOf(binding.role.text.toString()),
                        binding.clientId.text.toString()
                    )
                    val newUserUID = MainActivity.getAuth().currentUser?.uid ?: "no uid"
                    viewModel.updateUser(newUserUID, newUser)
                }
            })

        viewModel.updateUserResult.observe(viewLifecycleOwner,
            Observer { updateUserResult ->
                updateUserResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                updateUserResult.error?.let {
                    showSignUpFailed(it)
                }
                updateUserResult.success?.let {
                    updateUi(it)
                }
            })
    }

    private fun updateUi(model: String) {
        val welcome = getString(R.string.welcome) + " " + model
        Snackbar.make(requireView(), welcome, Snackbar.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_signUpFragment_pop)
    }

    private fun showSignUpFailed(@StringRes errorString: Int) {
        Snackbar.make(requireView(), errorString, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * ViewModel provider factory to instantiate SignUpViewModel.
     * Required given SignUpViewModel has a non-empty constructor
     */
    class SignUpViewModelFactory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(
                    signUpDataSink = SignUpDataSink()
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}