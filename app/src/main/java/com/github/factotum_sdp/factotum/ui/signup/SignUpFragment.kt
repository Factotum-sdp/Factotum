package com.github.factotum_sdp.factotum.ui.signup

import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.data.*
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance.getAuth
import com.github.factotum_sdp.factotum.databinding.FragmentSignupBinding
import com.github.factotum_sdp.factotum.model.Role
import com.github.factotum_sdp.factotum.model.User
import com.github.factotum_sdp.factotum.repositories.LoginRepository
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private lateinit var viewModel: SignUpViewModel
    private var _binding: FragmentSignupBinding? = null

    private val roles = listOf("BOSS", "COURIER", "CLIENT")
    private lateinit var adapter: ArrayAdapter<String>

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        //(activity as AppCompatActivity).supportActionBar?.title = getString(R.string.sign_up)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(this, SignUpViewModelFactory(requireContext()))[SignUpViewModel::class.java]

        val usernameEditText = binding.username
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val roleAutoCompleteTextView = binding.role
        val loadingProgressBar = binding.loading
        val signUpButton = binding.signup

        observeSignUpFormState(
            signUpButton,
            usernameEditText,
            emailEditText,
            passwordEditText
        )

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

        listenToAuthButton(signUpButton, loadingProgressBar, emailEditText, passwordEditText)

        observeAuthResult()
        observeUpdateUserResult(loadingProgressBar)

        adapter = ArrayAdapter(requireContext(), R.layout.user_role_item, roles)

        roleAutoCompleteTextView.setAdapter(adapter)
    }

    private fun observeSignUpFormState(
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

    private fun observeAuthResult() {
        viewModel.authResult.observe(viewLifecycleOwner,
            Observer { authResult ->
                authResult ?: return@Observer
                authResult.error?.let {
                    showSignUpFailed(it)
                }
                authResult.success?.let {
                    updateUi(it)
                }
            })
    }

    private fun observeUpdateUserResult(loadingProgressBar: View) {
        viewModel.updateUserResult.observe(viewLifecycleOwner, Observer { updateUserResult ->
            updateUserResult ?: return@Observer
            loadingProgressBar.visibility = View.GONE
            updateUserResult.error?.let {
                showSignUpFailed(it)
            }
            updateUserResult.success?.let {
                (requireActivity() as MainActivity).restartAppActivity()
            }
        })
    }

    private fun updateUi(model: String) {
        val welcome = getString(R.string.welcome) + " " + model
        Snackbar.make(requireView(), welcome, Snackbar.LENGTH_LONG).show()
        val newUserUID = getAuth().currentUser?.uid ?: "no uid"
        val newUser = User(
            uid=newUserUID,
            name=binding.username.text.toString(),
            email=binding.email.text.toString(),
            role=Role.valueOf(binding.role.text.toString())
        )
        viewModel.updateUser(newUserUID, newUser)
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
    class SignUpViewModelFactory (private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
                return SignUpViewModel(
                    signUpDataSink = SignUpDataSink(),
                    loginRepository = LoginRepository(LoginDataSource(), context)
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}