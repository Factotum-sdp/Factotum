package com.github.factotum_sdp.factotum.placeholder

import com.github.factotum_sdp.factotum.data.model.Role
import com.github.factotum_sdp.factotum.data.model.User

/**
 * Temporary PlaceHolder object for fake users data
 * For testing purpose of User data displayed in the Nav menu header
 */
object UsersPlaceHolder {
    val USER1 =
        User("00-00", "Valentino Rossi", "valentino.rossi@epfl.ch", Role.BOSS)
    val USER2 =
        User("12-34", "Fabian Cancellara", "fabian.cancellara@tour-de-france.fr", Role.COURIER)
    val USER3 =
        User("56-78", "Tadej Pogacar", "tadej.pogacar@la-vuuelta.es", Role.COURIER)
}