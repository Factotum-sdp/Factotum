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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.databinding.FragmentSignupBinding

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
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]

        val usernameEditText = binding.username
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val roleAutoCompleteTextView = binding.role
        val signUpButton = binding.signup

        observe(signUpButton, usernameEditText, emailEditText, passwordEditText)

        val afterTextChangedListener = createTextWatcher(
            viewModel,
            usernameEditText,
            emailEditText,
            passwordEditText,
            roleAutoCompleteTextView
        )

        addListeners(
            usernameEditText,
            emailEditText,
            passwordEditText,
            roleAutoCompleteTextView,
            afterTextChangedListener
        )

        adapter = ArrayAdapter(requireContext(), R.layout.user_role_item, roles)

        roleAutoCompleteTextView.setAdapter(adapter)
    }

    private fun observe(
        signupButton: Button,
        usernameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText
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
            })
    }

    private fun createTextWatcher(
        viewModel: SignUpViewModel,
        usernameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        roleAutoCompleteTextView: EditText
    ): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                viewModel.signUpDataChanged(
                    usernameEditText.text.toString(),
                    emailEditText.text.toString(),
                    passwordEditText.text.toString(),
                    roleAutoCompleteTextView.text.toString()
                )
            }
        }
    }

    private fun addListeners(
        usernameEditText: EditText,
        emailEditText: EditText,
        passwordEditText: EditText,
        roleAutoCompleteTextView: EditText,
        afterTextChangedListener: TextWatcher
    ) {
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        emailEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        roleAutoCompleteTextView.addTextChangedListener(afterTextChangedListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}