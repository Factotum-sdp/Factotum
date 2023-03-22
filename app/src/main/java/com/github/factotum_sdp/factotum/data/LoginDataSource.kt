package com.github.factotum_sdp.factotum.data

import android.util.Log
import com.github.factotum_sdp.factotum.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val auth = FirebaseAuth.getInstance()

    fun login(userEmail: String, password: String): Result<LoggedInUser> {
        val authResultFuture = CompletableFuture<Result<LoggedInUser>>()

        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val loggedInUser =
                        LoggedInUser(auth.currentUser!!.uid, auth.currentUser!!.email!!)
                    Log.d("LOGIN", "UID:" + auth.currentUser!!.uid)
                    Log.d("LOGIN", "Email:" + auth.currentUser!!.email)
                    authResultFuture.complete(Result.Success(loggedInUser))
                } else {
                    Log.d("LOGIN", "Error logging in", authTask.exception)
                    authResultFuture.complete(
                        Result.Error(
                            IOException(
                                "Error logging in",
                                authTask.exception
                            )
                        )
                    )
                }
            }

        return authResultFuture.get()
    }

    fun logout(auth: FirebaseAuth) {
        auth.signOut()
    }

}