package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.model.Result
import com.github.factotum_sdp.factotum.model.Role
import com.github.factotum_sdp.factotum.model.User
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val auth = FirebaseInstance.getAuth()
    private val dbRef = FirebaseInstance.getDatabase().reference

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
                uid=uid,
                name=it.child("name").value as String,
                email=it.child("email").value as String,
                role=Role.valueOf(it.child("role").value as String),
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

    fun logout() {
        auth.signOut()
    }

    companion object {
        const val DISPATCH_DB_PATH: String = "profile-dispatch"
    }
}
