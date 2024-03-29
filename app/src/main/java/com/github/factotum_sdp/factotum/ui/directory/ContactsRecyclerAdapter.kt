package com.github.factotum_sdp.factotum.ui.directory

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.model.Contact
import com.github.factotum_sdp.factotum.model.Role
import com.github.factotum_sdp.factotum.ui.directory.DirectoryFragment.Companion.USERNAME_NAV_KEY
import com.google.android.material.card.MaterialCardView

class ContactsRecyclerAdapter : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder>(),
    Filterable {

    private var originalContacts: List<Contact> = emptyList()
    private var filteredContacts = ArrayList<Contact>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(contacts: List<Contact>) {
        originalContacts = contacts
        originalContacts = originalContacts.sortedWith(compareBy(
            { it.surname.lowercase() },
            { it.name.lowercase() },
            { it.username.lowercase() }
        ))
        filteredContacts = ArrayList(originalContacts)
        notifyDataSetChanged()
    }

    class ContactsViewHolder(itemView : View) :
        RecyclerView.ViewHolder(itemView) { //this is the view holder for the recycler view

        private val cardView: MaterialCardView
        val itemName: TextView
        val itemUsername: TextView
        val itemImage: ImageView

        init {
            itemName = itemView.findViewById(R.id.contact_surname_and_name)
            itemImage = itemView.findViewById(R.id.contact_image)
            itemUsername = itemView.findViewById(R.id.contact_username)
            cardView = itemView.findViewById(R.id.contact_card)

            cardView.setOnClickListener {  // Now we're setting the click listener to the card view
            it.findNavController().navigate(
                R.id.action_directoryFragment_to_contactDetailsFragment2,
                Bundle().apply {
                    putString(
                        USERNAME_NAV_KEY,
                        itemUsername.text.toString().substring(1)
                    )
                })
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ContactsViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(
                R.layout.single_contact_item,
                viewGroup,
                false
            ) //inflate the layout for each contact
        return ContactsViewHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ContactsViewHolder, i: Int) {
        holder.itemName.text = filteredContacts[i].surname + "  " + filteredContacts[i].name
        holder.itemUsername.text = "@" + filteredContacts[i].username

        val imageResource = getRoleImageResource(filteredContacts[i].role)
        holder.itemImage.setImageResource(imageResource)
    }

    private fun getRoleImageResource(role : String): Int {
        return when (role) {
            "BOSS" ->  R.mipmap.ic_boss_profile_pic_round
            "COURIER" -> R.mipmap.ic_courier_profile_pic_round
            "CLIENT" -> R.mipmap.ic_client_profile_pic_round
            else -> R.mipmap.ic_launcher_round
        }
    }

    override fun getItemCount(): Int { //simple getter for the number of contacts
        return filteredContacts.size
    }

    private fun cleanString(string: String): String {
        return string.lowercase().replace("\\s".toRegex(), "")
    }

    /**
     * This method is called when the user types in the search bar. It filters the contacts based on the constraint.
     */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = ArrayList<Contact>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(originalContacts) // Use the originalContacts list here
                } else {
                    // remove all whitespace from the constraint and the names such that names are matched even if they have different whitespace
                    val filterPattern = cleanString(constraint.toString())
                    for (item in originalContacts) { // Use the originalContacts list for filtering
                        val nameSurname = cleanString(item.name + item.surname)
                        val surnameName = cleanString(item.surname + item.name)
                        val username = cleanString(item.username)
                        if (nameSurname.contains(filterPattern) ||
                            surnameName.contains(filterPattern) ||
                            username.contains(filterPattern)
                        ) {
                            filteredList.add(item)
                        }
                    }
                }
                // Create a new FilterResults object to return
                val results = FilterResults()
                results.values = filteredList
                results.count = filteredList.size
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                Log.d("filter", "Results : " + results?.values.toString())
                filteredContacts.clear()
                filteredContacts.addAll(results?.values as ArrayList<Contact>)
                Log.d("filter", "Contacts : " + filteredContacts.joinToString { it.name })
                notifyDataSetChanged()
            }
        }
    }
}