package com.github.factotum_sdp.factotum.repositories

import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.Result
import com.github.factotum_sdp.factotum.models.User

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository(private val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    private var uid: String? = null
    private var user: User? = null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun login(userEmail: String, password: String): Result<String> {
        return dataSource.login(userEmail, password)
    }

    fun isLoggedIn(): Boolean {
        return user != null
    }

    fun getLoggedInUser(): User? {
        return user
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun retrieveUserFromDB(uid: String): Result<User> {
        val result = dataSource.retrieveUser(uid)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: User) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}