package com.github.factotum_sdp.factotum.ui.directory

import com.google.firebase.database.FirebaseDatabase

fun emptyFirebaseDatabase(database: FirebaseDatabase) {
    database.reference.child("contacts").removeValue()
}