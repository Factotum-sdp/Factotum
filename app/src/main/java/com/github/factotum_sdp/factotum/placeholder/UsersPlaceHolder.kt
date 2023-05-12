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
        uid = "user1",
        name="Valentino Rossi",
        email="valentino.rossi@epfl.ch",
        role=Role.BOSS,
        password = password
    )
    val USER2 = UserWithPassword(
        uid = "user2",
        name = "Marc Marquez",
        email = "marc.marquez@epfl.ch",
        role = Role.BOSS,
        password = password
    )
    val USER3 = UserWithPassword(
        uid = "user3",
        name = "Jane Doe",
        email = "jane.doe@gmail.com",
        role = Role.BOSS,
        password = password
    )
    val USER_BOSS = UserWithPassword(
        uid = "user4",
        name = "Boss",
        email = "boss@gmail.com",
        role = Role.BOSS,
        password = password
    )
    val USER_COURIER = UserWithPassword(
        uid = "user5",
        name = "Courier",
        email = "courier@gmail.com",
        role = Role.COURIER,
        password = password
    )
    val USER_CLIENT = UserWithPassword(
        uid = "user6",
        name = "Client",
        email = "client@gmail.com",
        role = Role.CLIENT,
        password = password
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
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    data class UserWithPassword(
        val uid: String,
        val name: String,
        val email: String,
        val role: Role,
        val password: String
    )
}