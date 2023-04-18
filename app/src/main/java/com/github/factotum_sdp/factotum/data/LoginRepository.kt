package com.github.factotum_sdp.factotum.data

import java.io.IOException

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository(private val dataSource: LoginDataSource) {

    private val dataSink = SignUpDataSink()

    // in-memory cache of the loggedInUser object
    private var user: User? = null

    private var usersList: List<User>? = null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
        usersList = null
    }

    fun login(userEmail: String, password: String): Result<User> {
        val profile = usersList?.find { it.email == userEmail }
            ?: return Result.Error(IOException("User not found"))

        val result = dataSource.login(userEmail, password, profile)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    fun retrieveUsersList(): Result<List<User>> {
        val result = dataSource.retrieveUsersList()

        if (result is Result.Success) {
            setUsersList(result.data)
        }

        return result
    }

    fun updateUserList(user: User): Result<String> {
        val temporaryList = usersList?.plus(user) ?: listOf(user)
        val result = dataSink.updateUsersList(temporaryList)

        if (result is Result.Success) {
            usersList = temporaryList
        }

        return result
    }

    private fun setLoggedInUser(loggedInUser: User) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    private fun setUsersList(usersList: List<User>) {
        this.usersList = usersList
    }
}