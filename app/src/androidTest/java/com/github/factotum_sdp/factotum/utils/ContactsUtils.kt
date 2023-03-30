package com.github.factotum_sdp.factotum.utils

import com.google.firebase.database.FirebaseDatabase

class ContactsUtils {
    companion object {
        fun emptyFirebaseDatabase(database: FirebaseDatabase) {
            database.reference.child("contacts").removeValue()
        }
    }
}