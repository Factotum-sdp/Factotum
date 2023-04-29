package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.MainActivity
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val auth = MainActivity.getAuth()
    private val dbRef = MainActivity.getDatabase().reference

    fun login(userEmail: String, password: String): Result<String> {
        val authResultFuture = CompletableFuture<Result<String>>()

        auth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                authResultFuture.complete(Result.Success(auth.currentUser?.uid ?: "no uid"))
            } else {
                authResultFuture.complete(
                    Result.Error(
                        IOException(
                            "Error logging in", authTask.exception
                        )
                    )
                )
            }
        }

        return authResultFuture.get()
    }

    fun retrieveUser(uid: String): Result<User> {
        val profilesResultFuture = CompletableFuture<Result<User>>()
        dbRef.child(DISPATCH_DB_PATH).child(uid).get().addOnSuccessListener {
            if (!it.exists()) {
                profilesResultFuture.complete(
                    Result.Error(IOException("Error retrieving user"))
                )
                return@addOnSuccessListener
            }
            val user = User(
                it.child("name").value as String,
                it.child("email").value as String,
                Role.valueOf(it.child("role").value as String)
            )
            auth.currentUser?.updateProfile(
                UserProfileChangeRequest.Builder()
                    .setDisplayName(user.name).build()
            )
            profilesResultFuture.complete(Result.Success(user))
        }.addOnFailureListener {
            profilesResultFuture.complete(
                Result.Error(IOException("Error retrieving user", it))
            )
        }
        return profilesResultFuture.get()
    }

    companion object {
        const val DISPATCH_DB_PATH: String = "profile-dispatch"
    }
}
