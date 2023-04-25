package com.github.factotum_sdp.factotum.data

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository(private val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    private var user: User? = null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun login(userEmail: String, password: String): Result<String> {
        return dataSource.login(userEmail, password)
    }

    fun retrieveUser(uid: String): Result<User> {
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