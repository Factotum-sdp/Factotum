package com.github.factotum_sdp.factotum.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val auth = FirebaseAuth.getInstance()
    private val dbRef = Firebase.database.reference

    fun login(userEmail: String, password: String): Result<LoggedInUser> {
        val authResultFuture = CompletableFuture<Result<LoggedInUser>>()

        val roles = retrieveRoles(authResultFuture)

        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val loggedInUser =
                        LoggedInUser(
                            auth.currentUser!!.uid,
                            auth.currentUser!!.email!!,
                            roles[auth.currentUser!!.email!!] ?: Role.CLIENT
                        )
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

    private fun retrieveRoles(authResultFuture: CompletableFuture<Result<LoggedInUser>>): Map<String, Role> {
        var roles: Map<String, Role> = mapOf()
        dbRef.child("profile-dispatch").get().addOnSuccessListener {
            val profiles = it.value as List<*>
            roles = profiles.associate { profile ->
                val profileMap = profile as Map<*, *>
                val email = profileMap["email"] as String
                val role = Role.valueOf(profileMap["role"] as String)
                email to role
            }
        }.addOnFailureListener {
            authResultFuture.complete(
                Result.Error(
                    IOException(
                        "Error fetching profiles",
                        it
                    )
                )
            )
        }
        return roles
    }

}