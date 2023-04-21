package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.MainActivity
import java.io.IOException
import java.util.concurrent.CompletableFuture

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {
    private val auth = MainActivity.getAuth()
    private val dbRef = MainActivity.getDatabase().reference

    fun login(userEmail: String, password: String, user: User): Result<User> {
        val authResultFuture = CompletableFuture<Result<User>>()

        auth.signInWithEmailAndPassword(userEmail, password).addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    authResultFuture.complete(Result.Success(user))
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

    fun retrieveUsersList(): Result<MutableList<User>> {
        val profilesResultFuture = CompletableFuture<Result<MutableList<User>>>()
        var usersList: MutableList<User>
        dbRef.child(DISPATCH_DB_PATH).get().addOnSuccessListener {
            if (!it.exists()) {
                profilesResultFuture.complete(
                    Result.Error(IOException("Error retrieving users"))
                )
                return@addOnSuccessListener
            }
            val profileList = (it.value as Map<*, *>).values.toList()
            usersList = profilesToUsers(profileList)
            profilesResultFuture.complete(Result.Success(usersList))
        }.addOnFailureListener {
            profilesResultFuture.complete(
                Result.Error(IOException("Error retrieving users", it))
            )
        }
        return profilesResultFuture.get()
    }

    private fun profilesToUsers(usersList: List<*>): MutableList<User> {
        return usersList.map { user ->
            val userMap = user as Map<*, *>
            val displayName = userMap["name"] as String
            val email = userMap["email"] as String
            val role = Role.valueOf(userMap["role"] as String)
            User(displayName, email, role)
        }.toMutableList()
    }

    companion object {
        const val DISPATCH_DB_PATH: String = "profile-dispatch"
    }
}