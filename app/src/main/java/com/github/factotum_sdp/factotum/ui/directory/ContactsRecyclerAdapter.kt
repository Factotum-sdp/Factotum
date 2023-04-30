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
import com.github.factotum_sdp.factotum.placeholder.Contact

class ContactsRecyclerAdapter : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder>(),
    Filterable {

    private var originalContacts: List<Contact> = emptyList()
    private var filteredContacts = ArrayList<Contact>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateContacts(contacts: List<Contact>) {
        originalContacts = contacts
        originalContacts = originalContacts.sortedBy { it.surname + it.name + it.username }
        filteredContacts = ArrayList(originalContacts)
        notifyDataSetChanged()
    }

    class ContactsViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) { //this is the view holder for the recycler view

        val itemName: TextView
        val itemUsername: TextView
        val itemImage: ImageView

        init {
            itemName = itemView.findViewById(R.id.contact_surname_and_name)
            itemImage = itemView.findViewById(R.id.contact_image)
            itemUsername = itemView.findViewById(R.id.contact_username)

            itemView.setOnClickListener {   //when a contact is clicked, go to the contact details fragment
                itemView.findNavController().navigate(
                    R.id.action_directoryFragment_to_contactDetailsFragment2,
                    Bundle().apply {
                        putString(
                            "username",
                            itemUsername.text.toString().substring(1)
                        ) // pass the id of the contact to the contact details fragment so that it can display the correct contact
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
        holder.itemImage.setImageResource(filteredContacts[i].profile_pic_id)
    }

    override fun getItemCount(): Int { //simple getter for the number of contacts
        return filteredContacts.size
    }

    private fun cleanString(string: String): String {
        return string.lowercase().replace("\\s".toRegex(), "")
    }

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