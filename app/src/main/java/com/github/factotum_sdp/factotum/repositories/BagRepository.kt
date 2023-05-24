package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.models.Bag
import com.github.factotum_sdp.factotum.models.Pack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class BagRepository(remoteSource: DatabaseReference, username: String, localSource: DataStore<Bag>)
    : BackUpRepository<Bag>(remoteSource, username, localSource) {

    override fun extractFromSnapshot(snapshot: DataSnapshot): Bag {
        val packs = snapshot.children.mapNotNull {
            it.getValue(Pack::class.java)
        }
        return Bag(packs)
    }

}