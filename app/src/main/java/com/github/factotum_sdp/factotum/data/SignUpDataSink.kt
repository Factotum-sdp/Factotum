package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.data.LoginDataSource.Companion.DISPATCH_DB_PATH
import java.io.IOException
import java.util.concurrent.CompletableFuture

class SignUpDataSink {
    private val auth = MainActivity.getAuth()
    private val dbRef = MainActivity.getDatabase().reference

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

    fun updateUsersList(user: User): Result<String> {
        val updateUsersResultFuture = CompletableFuture<Result<String>>()

        dbRef.child(DISPATCH_DB_PATH).push().setValue(user)
            .addOnSuccessListener {
                updateUsersResultFuture.complete(Result.Success("Success updating users"))
            }.addOnFailureListener {
                updateUsersResultFuture.complete(
                    Result.Error(IOException("Error updating profiles", it))
                )
            }
        return updateUsersResultFuture.get()
    }
}