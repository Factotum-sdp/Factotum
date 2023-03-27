package com.github.factotum_sdp.factotum.data

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
                    authResultFuture.complete(Result.Success(loggedInUser))
                } else {
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

}