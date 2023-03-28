package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.data.Role
import com.github.factotum_sdp.factotum.data.LoggedInUser

/**
 * Temporary PlaceHolder object for fake users data
 * For testing purpose of User data displayed in the Nav menu header
 */
object UsersPlaceHolder {
    val USER1 =
        LoggedInUser("Valentino Rossi", "valentino.rossi@epfl.ch", Role.BOSS)
    val USER2 =
        LoggedInUser("Fabian Cancellara", "fabian.cancellara@tour-de-france.fr", Role.COURIER)
    val USER3 =
        LoggedInUser( "Tadej Pogacar", "tadej.pogacar@la-vuuelta.es", Role.COURIER)
}