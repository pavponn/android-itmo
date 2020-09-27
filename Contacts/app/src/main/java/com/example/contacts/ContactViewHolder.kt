package com.example.contacts

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.contact_item.view.*

class ContactViewHolder(val root: View) : RecyclerView.ViewHolder(root) {
    fun bind(contact: Contact) {
        with(root) {
            contact_name.text = contact.name
            contact_phone_number.text =  contact.phoneNumber
        }
    }
}