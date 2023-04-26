package com.github.factotum_sdp.factotum.placeholder

import android.util.Log
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.LoginDataSource.Companion.DISPATCH_DB_PATH
import com.github.factotum_sdp.factotum.data.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CompletableDeferred
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Temporary PlaceHolder object for fake users data
 * For testing purpose of User data displayed in the Nav menu header
 */
object UsersPlaceHolder {

    private val users = mutableListOf<User>()
    private lateinit var dataSource: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    private const val password = "123456"
    val USER1 = User(
        "Valentino Rossi",
        "valentino.rossi@epfl.ch",
        Role.BOSS,
        password
    )
    val USER2 = User(
        "Marc Marquez",
        "marc.marquez@epfl.ch",
        Role.BOSS,
        password
    )
    val USER3 = User(
        "Jane Doe",
        "jane.doe@gmail.com",
        Role.BOSS,
        password
    )
    val USER_BOSS = User(
        "Boss",
        "boss@gmail.com",
        Role.BOSS,
        password
    )
    val USER_COURIER = User(
        "Courier",
        "courier@gmail.com",
        Role.COURIER,
        password
    )
    val USER_CLIENT = User(
        "Client",
        "client@gmail.com",
        Role.CLIENT,
        password
    )

    fun init(dataSource: FirebaseDatabase, auth: FirebaseAuth) {
        this.dataSource = dataSource
        this.auth = auth
        users.add(USER1)
        users.add(USER2)
        users.add(USER3)
        users.add(USER_BOSS)
        users.add(USER_COURIER)
        users.add(USER_CLIENT)
    }

    /**
     * Populates the database with users.
     */
    suspend fun addUserToDb(user: User) = suspendCoroutine { continuation ->
        dataSource.getReference(DISPATCH_DB_PATH).push().setValue(user)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }


    suspend fun addAuthUser(user: User) = suspendCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(user.email, password).addOnSuccessListener {
            continuation.resume(Unit)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }

    data class User(
        val name: String,
        val email: String,
        val role: Role,
        val password: String
    )
}