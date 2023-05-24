package com.github.factotum_sdp.factotum.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.UserViewModel
import com.github.factotum_sdp.factotum.databinding.FragmentLoginBinding
import com.github.factotum_sdp.factotum.models.User
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel
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

        val logVMFact = LoginViewModel.LoginViewModelFactory(requireContext())
        viewModel =
            ViewModelProvider(this, logVMFact)[LoginViewModel::class.java]
        
        // Define the UI elements
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val loginButton = binding.login
        val signupButton = binding.signup
        val loadingProgressBar = binding.loading

        // Observe the login result and show it in a snackbar
        observeAuthResult(loadingProgressBar)

        viewModel.checkIfCachedUser()?.let {
            userViewModel.setLoggedInUser(it)
        }

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

    private fun listenToAuthButton(
        authButton: Button,
        loadingProgressBar: ProgressBar,
        emailEditText: EditText,
        passwordEditText: EditText
    ) {
        authButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            viewModel.auth(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }

    private fun observeAuthResult(loadingProgressBar: View) {
        viewModel.authResult.observe(viewLifecycleOwner,
            Observer { authResult ->
                authResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                authResult.error?.let {
                    showLoginFailed(it)
                }
                authResult.success?.let {
                    viewModel.retrieveUser(it)
                }
            })

        viewModel.retrieveUsersResult.observe(viewLifecycleOwner,
            Observer { result ->
                result ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                result.error?.let {
                    showLoginFailed(it)
                }
                result.success?.let {
                    updateUi(it)
                }
            })
    }

    private fun updateUi(model: User) {
        userViewModel.setLoggedInUser(model)
        updateNGraphStartDestination()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Snackbar.make(requireView(), errorString, Snackbar.LENGTH_SHORT).show()
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