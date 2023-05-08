package com.github.factotum_sdp.factotum.models

import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseDateFormatted
import com.github.factotum_sdp.factotum.firebase.FirebaseStringFormat.firebaseSafeString
import com.github.factotum_sdp.factotum.serializers.FirebaseDateKSerializer
import com.github.factotum_sdp.factotum.ui.roadbook.DRecordList
import com.google.firebase.database.DatabaseReference
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class Shift(@Serializable(with = FirebaseDateKSerializer::class)val date: Date = Date(), val user: User = User("FAKE_USER", "fake_user@donotuse.com", Role.UNKNOWN), val records: DRecordList = DRecordList(
    emptyList()
)
){
    companion object {
        fun shiftDbPathFromRoot(ref : DatabaseReference, shift: Shift) : DatabaseReference{
            return ref.child(firebaseSafeString(shift.user.name))
                .child(firebaseDateFormatted(shift.date))
        }
    }
}