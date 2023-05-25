package com.github.factotum_sdp.factotum.repositories
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.model.Result
import com.github.factotum_sdp.factotum.model.User
import com.google.gson.Gson

class LoginRepository(private val dataSource: LoginDataSource, context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val pref = EncryptedSharedPreferences.create(
        context,
        "user_pref",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // in-memory cache of the loggedInUser object
    private var user: User? = null

    init {
        user = getUserFromPref()
    }

    fun login(userEmail: String, password: String): Result<String> {
        return dataSource.login(userEmail, password)
    }

    fun getLoggedInUser(): User? {
        return user
    }

    fun logout() {
        user = null
        clearUserFromPref()
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
        saveUserToPref(loggedInUser)
    }

    private fun saveUserToPref(user: User) {
        val userJson = Gson().toJson(user)
        pref.edit().putString("logged_in_user", userJson).apply()
    }

    private fun getUserFromPref(): User? {
        val userJson = pref.getString("logged_in_user", null)
        return if (userJson != null) Gson().fromJson(userJson, User::class.java) else null
    }

    private fun clearUserFromPref() {
        pref.edit().remove("logged_in_user").apply()
    }
}
