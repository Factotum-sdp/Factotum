package com.github.factotum_sdp.factotum.data

import com.github.factotum_sdp.factotum.data.model.Role
import com.github.factotum_sdp.factotum.data.model.User
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<User> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = User(java.util.UUID.randomUUID().toString(), username,
                "$username@gmail.com", Role.COURIER )
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}