package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.data.LoginDataSource.Companion.DISPATCH_DB_PATH
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.User
import java.io.IOException
import java.util.concurrent.CompletableFuture

class SignUpDataSink {
    private val auth = FirebaseInstance.getAuth()
    private val dbRef = FirebaseInstance.getDatabase().reference

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

    fun updateUser(userUID: String, user: User): Result<String> {
        val updateUserResultFuture = CompletableFuture<Result<String>>()

        dbRef.child(DISPATCH_DB_PATH).child(userUID).setValue(user)
            .addOnSuccessListener {
                updateUserResultFuture.complete(Result.Success(user.name))
            }.addOnFailureListener {
                updateUserResultFuture.complete(
                    Result.Error(IOException("Error updating users", it))
                )
            }
        return updateUserResultFuture.get()
    }

    fun fetchUsername(username: String): Result<String> {
        val fetchUsernameFuture = CompletableFuture<Result<String>>()
        dbRef.child("contacts")
            .child(username)
            .get().addOnSuccessListener {
                if (!it.exists()) {
                    fetchUsernameFuture.complete(Result.Error(IOException("Username doesn't exist")))
                }else{
                    fetchUsernameFuture.complete(Result.Success(username))
                }
            }.addOnFailureListener {
                fetchUsernameFuture.complete(Result.Error(IOException("Connection to database impossible")))
            }
        return fetchUsernameFuture.get()
    }
}