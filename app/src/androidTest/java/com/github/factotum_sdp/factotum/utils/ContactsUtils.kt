package com.github.factotum_sdp.factotum.utils

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.google.firebase.database.FirebaseDatabase
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class ContactsUtils {
    companion object {
        fun emptyFirebaseDatabase(database: FirebaseDatabase) {
            database.reference.child("contacts").removeValue()
        }

        fun withHolderContactName(name: String): Matcher<RecyclerView.ViewHolder> {
            return object : TypeSafeMatcher<RecyclerView.ViewHolder>() {
                var isFirstMatch = true

                override fun describeTo(description: Description) {
                    description.appendText("RecyclerView holder with contact name: $name")
                }

                override fun matchesSafely(item: RecyclerView.ViewHolder): Boolean {
                    val holderName =
                        item.itemView.findViewById<TextView>(R.id.contact_name).text.toString()
                    if (holderName == name && isFirstMatch) {
                        isFirstMatch = false
                        return true
                    }
                    return false
                }
            }
        }
    }
}