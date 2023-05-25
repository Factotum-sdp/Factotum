package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.model.Bag
import com.github.factotum_sdp.factotum.model.Pack
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

/**
 * The Bag repository
 *
 * In charge of being the unique place where to fetch and push Bag Data, i.e
 * a List<Pack>.
 *
 */
class BagRepository(remoteSource: DatabaseReference, username: String, localSource: DataStore<Bag>)
    : BackUpRepository<Bag>(remoteSource, username, localSource) {

    override fun extractFromSnapshot(snapshot: DataSnapshot): Bag {
        val packs = snapshot.children.mapNotNull {
            it.getValue(Pack::class.java)
        }
        return Bag(packs)
    }

}