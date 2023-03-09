package com.github.factotum_sdp.factotum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.factotum_sdp.factotum.contacts_content.ContactsList

class ContactsRecyclerAdapter : RecyclerView.Adapter<ContactsRecyclerAdapter.ContactsViewHolder>() {

    private val contacts = listOf(
        ContactsList.Contact("1", "John", "Lennon"),
        ContactsList.Contact("2", "Paul", "McCartney"),
        ContactsList.Contact("3", "George", "Harrison"),
        ContactsList.Contact("4", "Ringo", "Starr"),
        //create more random contacts
        ContactsList.Contact("5", "John", "Lennon"),
        ContactsList.Contact("6", "Paul", "McCartney"),
        ContactsList.Contact("7", "George", "Harrison"),
        ContactsList.Contact("8", "Ringo", "Starr"),
        ContactsList.Contact("9", "John", "Lennon"),
        ContactsList.Contact("10", "Paul", "McCartney"),
        ContactsList.Contact("11", "George", "Harrison"),
        ContactsList.Contact("12", "Ringo", "Starr"),
        ContactsList.Contact("13", "John", "Lennon"),
    )

    private val image = R.drawable.contact_image

    inner class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemName : TextView
        var itemSurname : TextView
        var itemImage : ImageView

        init {
            itemName = itemView.findViewById(R.id.contact_name)
            itemSurname = itemView.findViewById(R.id.contact_surname)
            itemImage = itemView.findViewById(R.id.contact_image)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ContactsViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.single_contact_item, viewGroup, false)
        return ContactsViewHolder(v)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, i: Int) {
        holder.itemName.text = contacts[i].name
        holder.itemSurname.text = contacts[i].surname
        holder.itemImage.setImageResource(image)
    }

    override fun getItemCount(): Int {
        return contacts.size
    }
}