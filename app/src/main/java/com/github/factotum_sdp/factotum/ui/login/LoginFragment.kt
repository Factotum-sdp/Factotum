package com.github.factotum_sdp.factotum.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.data.Role
import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.databinding.FragmentLoginBinding
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthFragment
import com.google.android.material.snackbar.Snackbar


class LoginFragment : BaseAuthFragment() {

    override lateinit var viewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    private val userViewModel: UserViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val logVMFact = LoginViewModel.LoginViewModelFactory(userViewModel)
        viewModel =
            ViewModelProvider(this, logVMFact)[LoginViewModel::class.java]

        // Define the UI elements
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val loginButton = binding.login
        val signupButton = binding.signup
        val loadingProgressBar = binding.loading
        val profileRetrieveErrorText = binding.profileRetrivalError

        // Retrieve profiles of all users in the database
        viewModel.retrieveUsersList()

        // Observe the result of retrieving profiles and show it in a snackbar.
        observeRetrieveProfilesResult(profileRetrieveErrorText)

        // Observe the login result and show it in a snackbar
        observeAuthResult(loadingProgressBar)

        val afterTextChangedListener =
            createTextWatcher(viewModel, emailEditText, passwordEditText)

        // Add listeners to edit text fields
        addListeners(emailEditText, passwordEditText, afterTextChangedListener)

        // Add listener to login button
        listenToAuthButton(loginButton, loadingProgressBar, emailEditText, passwordEditText)

        // Observe the login form state and enable/disable the login button accordingly
        observeLoginFormState(loginButton, emailEditText, passwordEditText)

        // Add listener to signup button
        signupButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun createTextWatcher(
        loginViewModel: LoginViewModel,
        usernameEditText: EditText,
        passwordEditText: EditText
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                loginViewModel.loginDataChanged(
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
    }

    private fun observeRetrieveProfilesResult(profileRetrieveErrorText: TextView) {
        viewModel.retrieveUsersResult.observe(viewLifecycleOwner) { usersResult ->
            usersResult ?: return@observe
            usersResult.error?.let {
                profileRetrieveErrorText.visibility = View.VISIBLE
            }
        }
    }

    private fun observeLoginFormState(
        loginButton: Button,
        usernameEditText: EditText,
        passwordEditText: EditText
    ) {
        viewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                loginFormState.emailError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })
    }

    private fun addListeners(
        emailEditText: EditText,
        passwordEditText: EditText,
        afterTextChangedListener: TextWatcher
    ) {
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
    }

    override fun updateUi(model: Any) {
        val welcome = getString(R.string.welcome) + " " + (model as User).displayName + "!"
        Snackbar.make(requireView(), welcome, Snackbar.LENGTH_LONG).show()
        updateNGraphStartDestination()
    }

    private fun updateNGraphStartDestination() {
        findNavController().navigate(R.id.action_loginFragment_to_roadBookFragment2)
        val navGraph = findNavController().navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.roadBookFragment)
        findNavController().graph = navGraph
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}