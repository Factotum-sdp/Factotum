package com.github.factotum_sdp.factotum.ui.auth

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar

abstract class BaseAuthFragment : Fragment() {

    abstract val viewModel: BaseAuthViewModel

    internal fun observeAuthResult(loadingProgressBar: View) {
        viewModel.authResult.observe(viewLifecycleOwner,
            Observer { authResult ->
                authResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                authResult.error?.let {
                    showSignUpFailed(it)
                }
                authResult.success?.let {
                    updateUi(it)
                }
            })
    }

    internal fun listenToAuthButton(
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

    abstract fun updateUi(model: Any)
    private fun showSignUpFailed(@StringRes errorString: Int) {
        Snackbar.make(requireView(), errorString, Snackbar.LENGTH_SHORT).show()
    }
}