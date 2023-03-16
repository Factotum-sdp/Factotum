package com.github.factotum_sdp.factotum.placeholder

/**
 * Temporary PlaceHolder object for fake users data
 * For testing purpose of User data displayed in the Nav menu header
 */
object UsersPlaceHolder {
    val USER1 = User("Valentino Rossi", "valentino.rossi@epfl.ch")

    data class User(
        val name: String,
        val email: String,
    )
}