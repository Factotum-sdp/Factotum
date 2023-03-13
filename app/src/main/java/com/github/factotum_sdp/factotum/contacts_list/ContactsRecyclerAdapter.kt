package com.github.factotum_sdp.factotum.contacts_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.R
import com.github.factotum_sdp.factotum.contacts_list.contacts_content.ContactsList

class ContactsRecyclerAdapter : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder>() {

    private val contacts = ContactsList.ITEMS

    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemRole : TextView
        var itemName : TextView
        var itemImage : ImageView

        init {
            itemRole = itemView.findViewById(R.id.contact_role)
            itemName = itemView.findViewById(R.id.contact_name)
            itemImage = itemView.findViewById(R.id.contact_image)

            itemView.setOnClickListener {
                itemView.findNavController().navigate(R.id.action_contactsFragment_to_contactDetailsFragment, Bundle().apply {
                    putInt("id", bindingAdapterPosition)
                })
            }
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ContactsViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.single_contact_item, viewGroup, false)
        return ContactsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, i: Int) {
        holder.itemRole.text = contacts[i].role
        holder.itemName.text = contacts[i].name
        holder.itemImage.setImageResource(contacts[i].profile_pic_id)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}