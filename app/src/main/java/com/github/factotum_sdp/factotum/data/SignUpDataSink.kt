package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.MainActivity
import java.io.IOException
import java.util.concurrent.CompletableFuture

class SignUpDataSink {
    private val auth = MainActivity.getAuth()

    fun signUp(email: String, password: String): Result<String> {
        val authResultFuture = CompletableFuture<Result<String>>()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    authResultFuture.complete(Result.Success(email))
                } else {
                    authResultFuture.complete(
                        Result.Error(
                            IOException(
                                "Error signing up",
                                authTask.exception
                            )
                        )
                    )
                }
            }
        return authResultFuture.get()
    }
}