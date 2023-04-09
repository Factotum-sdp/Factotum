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
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.data.User
import com.github.factotum_sdp.factotum.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

    private val userViewModel: UserViewModel by activityViewModels()
    private var isProfileRetrieved = false

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
        loginViewModel =
            ViewModelProvider(this, logVMFact)[LoginViewModel::class.java]

        // Define the UI elements
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val loginButton = binding.login
        val signupButton = binding.signup
        val loadingProgressBar = binding.loading
        val profileRetrieveErrorText = binding.profileRetrivalError

        // Retrieve profiles of all users in the database
        loginViewModel.retrieveProfiles()

        // Observe the result of retrieving profiles and show it in a snackbar.
        observeRetrieveProfilesResult(profileRetrieveErrorText)

        // Observe the login form state and enable/disable the login button accordingly
        observeLoginFormState(loginButton, emailEditText, passwordEditText)

        // Observe the login result and show it in a snackbar
        observeLoginResult(loadingProgressBar)

        val afterTextChangedListener =
            createTextWatcher(loginViewModel, emailEditText, passwordEditText)

        // Add listeners to edit text fields
        addListeners(emailEditText, passwordEditText, afterTextChangedListener)

        // Add listener to login button
        listenToLoginButton(loginButton, emailEditText, passwordEditText)

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
        loginViewModel.retrieveProfilesResult.observe(viewLifecycleOwner) { profileRetrievalResult ->
            profileRetrievalResult ?: return@observe
            profileRetrievalResult.error?.let {
                profileRetrieveErrorText.visibility = View.VISIBLE
            }
            profileRetrievalResult.success?.let {
                isProfileRetrieved = true
            }
        }
    }

    private fun observeLoginFormState(
        loginButton: Button,
        usernameEditText: EditText,
        passwordEditText: EditText
    ) {
        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid && isProfileRetrieved
                loginFormState.emailError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            })
    }

    private fun observeLoginResult(loadingProgressBar: View) {
        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let {
                    showSnackBar(it)
                }
                loginResult.success?.let {
                    updateUiWithUser(it)
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

    private fun listenToLoginButton(
        loginButton: Button,
        emailEditText: EditText,
        passwordEditText: EditText
    ) {
        loginButton.setOnClickListener {
            loginViewModel.login(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    private fun updateUiWithUser(loggedInUser: User) {
        val welcome = getString(R.string.welcome) + " " + loggedInUser.displayName + "!"
        Snackbar.make(requireView(), welcome, Snackbar.LENGTH_LONG).show()
        updateNGraphStartDestination()
    }

    private fun updateNGraphStartDestination() {
        findNavController().navigate(R.id.action_loginFragment_to_roadBookFragment2)
        val navGraph = findNavController().navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(R.id.roadBookFragment)
        findNavController().graph = navGraph
    }

    private fun showSnackBar(@StringRes message: Int) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}