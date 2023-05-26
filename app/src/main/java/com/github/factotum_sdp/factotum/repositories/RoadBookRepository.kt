package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.model.DestinationRecord
import com.github.factotum_sdp.factotum.model.DRecordList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference


/**
 * The RoadBook repository
 *
 * In charge of being the unique place where to fetch and push RoadBook Data, i.e
 * a DRecordList : List<DestinationRecord>.
 *
 */
class RoadBookRepository(remoteSource: DatabaseReference, username: String,
                         localSource: DataStore<DRecordList>): BackUpRepository<DRecordList>(remoteSource, username, localSource) {
    override fun extractFromSnapshot(snapshot: DataSnapshot): DRecordList {
        val records = snapshot.children.mapNotNull {
            it.getValue(DestinationRecord::class.java)
        }
        return DRecordList(records)
    }

    override fun setBackUp(data: DRecordList) {
        super.setBackUp(data.withArchived())
    }
}