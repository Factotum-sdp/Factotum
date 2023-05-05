package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.models.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Temporary PlaceHolder object for fake users data
 * For testing purpose of User data displayed in the Nav menu header
 */
object UsersPlaceHolder {

    private lateinit var dataSource: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private const val password = "123456"
    val USER1 = UserWithPassword(
        "Valentino Rossi",
        "valentino.rossi@epfl.ch",
        Role.BOSS,
        "vrossi",
        password,
    )
    val USER2 = UserWithPassword(
        "Marc Marquez",
        "marc.marquez@epfl.ch",
        Role.BOSS,
        "mmarquez",
        password
    )
    val USER3 = UserWithPassword(
        "Jane Doe",
        "jane.doe@gmail.com",
        Role.BOSS,
        "jdoe",
        password
    )
    val USER_BOSS = UserWithPassword(
        "Boss",
        "boss@gmail.com",
        Role.BOSS,
        "boss",
        password
    )
    val USER_COURIER = UserWithPassword(
        "Courier",
        "courier@gmail.com",
        Role.COURIER,
        "courier",
        password
    )
    val USER_CLIENT = UserWithPassword(
        "Client",
        "client@gmail.com",
        Role.CLIENT,
        "client",
        password
    )

    fun init(dataSource: FirebaseDatabase, auth: FirebaseAuth) {
        this.dataSource = dataSource
        this.auth = auth
    }

    /**
     * Populates the database with user.
     */
    suspend fun addUserToDb(user: UserWithPassword) = suspendCoroutine { continuation ->
        dataSource.getReference(LoginDataSource.DISPATCH_DB_PATH)
            .child(FirebaseInstance.getAuth().currentUser?.uid ?: "")
            .setValue(user)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }

        auth.signOut()
    }

    suspend fun addAuthUser(user: UserWithPassword) = suspendCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    data class UserWithPassword(
        val name: String,
        val email: String,
        val role: Role,
        val username: String,
        val password: String
    )
}