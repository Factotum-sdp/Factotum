package com.github.factotum_sdp.factotum.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentLoginBinding
import com.github.factotum_sdp.factotum.ui.auth.BaseAuthFragment
import com.google.android.material.snackbar.Snackbar


class LoginFragment : BaseAuthFragment() {

    override lateinit var viewModel: LoginViewModel
    private var _binding: FragmentLoginBinding? = null

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

        viewModel =
            ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val loginButton = binding.login
        val signupButton = binding.signup
        val loadingProgressBar = binding.loading

        observeLoginFormState(loginButton, usernameEditText, passwordEditText)

        observeAuthResult(loadingProgressBar)

        val afterTextChangedListener =
            createTextWatcher(viewModel, usernameEditText, passwordEditText)

        addListeners(usernameEditText, passwordEditText, afterTextChangedListener)

        authButtonOnClick(loginButton, loadingProgressBar, usernameEditText, passwordEditText)

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
        usernameEditText: EditText,
        passwordEditText: EditText,
        afterTextChangedListener: TextWatcher
    ) {
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
    }

    override fun updateUi(model: Any) {
        val welcome = getString(R.string.welcome) + " " + (model as LoggedInUserView).email
        Snackbar.make(requireView(), welcome, Snackbar.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_loginFragment_to_roadBookFragment2)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}