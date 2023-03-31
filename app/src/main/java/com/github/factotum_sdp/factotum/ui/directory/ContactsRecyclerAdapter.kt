package com.github.factotum_sdp.factotum.ui.directory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.placeholder.ContactsList

class ContactsRecyclerAdapter : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder>() {

    private val contacts = ContactsList.contacts //this is the list of contacts
    //we consider the list of contacts to be constant so we don't need to worry about updating the recycler view
    //list of contact only changes when the app is restarted

    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) { //this is the view holder for the recycler view

        val itemRole : TextView //these are the views that we want to display for each contact
        val itemName : TextView
        val itemImage : ImageView

        init {
            itemRole = itemView.findViewById(R.id.contact_role) //connect the views to the layout
            itemName = itemView.findViewById(R.id.contact_name)
            itemImage = itemView.findViewById(R.id.contact_image)

            itemView.setOnClickListener {   //when a contact is clicked, go to the contact details fragment
                itemView.findNavController().navigate(R.id.action_directoryFragment_to_contactDetailsFragment2, Bundle().apply {
                    putInt("id", bindingAdapterPosition) // pass the id of the contact to the contact details fragment so that it can display the correct contact
                })
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ContactsViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.single_contact_item, viewGroup, false) //inflate the layout for each contact
        return ContactsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, i: Int) {
        holder.itemRole.text = contacts[i].role //set the text for each view
        holder.itemName.text = contacts[i].name
        holder.itemImage.setImageResource(contacts[i].profile_pic_id)
    }

    override fun getItemCount(): Int { //simple getter for the number of contacts
        return contacts.size
    }
}