package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.MainActivity
import com.github.factotum_sdp.factotum.data.LoginDataSource
import com.github.factotum_sdp.factotum.data.Role
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CompletableDeferred

/**
 * Temporary PlaceHolder object for fake users data
 * For testing purpose of User data displayed in the Nav menu header
 */
object UsersPlaceHolder {

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
    }

    /**
     * Populates the database with user.
     */
    suspend fun addUserToDb(user: User) = suspendCoroutine { continuation ->
        dataSource.getReference(LoginDataSource.DISPATCH_DB_PATH)
            .child(MainActivity.getAuth().currentUser?.uid ?: "")
            .setValue(user)
            .addOnSuccessListener {
                continuation.complete(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.completeExceptionally(exception)
            }

        auth.signOut()
    }

    suspend fun addAuthUser(user: User) = suspendCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnSuccessListener {
                continuation.complete(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.completeExceptionally(exception)
            }
    }

    fun emptyFirebaseDatabase(database: FirebaseDatabase) {
        database.reference.child(LoginDataSource.DISPATCH_DB_PATH).removeValue()
    }

    data class User(
        val name: String,
        val email: String,
        val role: Role,
        val password: String
    )
}
