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

    fun login(userEmail: String, password: String, profile: User): Result<User> {
        val authResultFuture = CompletableFuture<Result<User>>()

        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    authResultFuture.complete(Result.Success(profile))
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

    fun retrieveProfiles(): Result<MutableList<User>> {
        val profilesResultFuture = CompletableFuture<Result<MutableList<User>>>()
        var profiles: MutableList<User>
        dbRef.child(DISPATCH_DB_PATH).get().addOnSuccessListener {
            if (!it.exists()) {
                profilesResultFuture.complete(
                    Result.Error(IOException("Error retrieving profiles"))
                )
                return@addOnSuccessListener
            }
            val profileList = it.value as List<*>
            profiles = profilesToUsers(profileList)
            profilesResultFuture.complete(Result.Success(profiles))
        }.addOnFailureListener {
            profilesResultFuture.complete(
                Result.Error(IOException("Error retrieving profiles", it))
            )
        }
        return profilesResultFuture.get()
    }

    private fun profilesToUsers(profileList: List<*>): MutableList<User> {
        return profileList.map { profile ->
            val profileMap = profile as Map<*, *>
            val displayName = profileMap["username"] as String
            val email = profileMap["email"] as String
            val role = Role.valueOf(profileMap["role"] as String)
            User(displayName, email, role)
        }.toMutableList()
    }

    companion object {
        const val DISPATCH_DB_PATH: String = "profile-dispatch"
    }
}