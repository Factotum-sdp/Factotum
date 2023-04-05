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

    fun login(userEmail: String, password: String, profile: User): Result<LoggedInUser> {
        val authResultFuture = CompletableFuture<Result<LoggedInUser>>()

        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val loggedInUser =
                        LoggedInUser(profile.displayName, profile.email, profile.role)
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

    fun retrieveProfiles(): Result<MutableList<User>> {
        /*val profilesResultFuture = CompletableFuture<Result<MutableList<User>>>()
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
        }*/
        return Result.Success(
            mutableListOf(
                User("Jane Doe", "jane.doe@gmail.com", Role.BOSS)
            )
        )
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
        private const val DISPATCH_DB_PATH: String = "profile-dispatch"
    }

    data class User(
        val displayName: String,
        val email: String,
        val role: Role
    )
}