package com.github.factotum_sdp.factotum.repositories

import androidx.datastore.core.DataStore
import com.github.factotum_sdp.factotum.firebase.FirebaseInstance
import com.github.factotum_sdp.factotum.models.DestinationRecord
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/**
 * The RoadBook repository
 *
 * In charge of being the unique place where to fetch and push RoadBook Data, i.e
 * a DRecordList : List<DestinationRecord>.
 *
 * It resolves the conflict between a "remoteSource" and a "localSource" of data, and enable caching
 * for both remote/network requests and local requests.
 *
 * If remoteSource is not online, we rely uniquely on the localSource, and don't use the first variable cache.
 * The first variable cache is used uniquely to avoid remoteSource update.
 * However, a second variable cache is used before fetching or update the localSource.
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