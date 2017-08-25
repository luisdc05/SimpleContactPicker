package com.luisdc05.simplecontactpicker.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.luisdc05.simplecontactpicker.R
import com.luisdc05.simplecontactpicker.model.ContactBase

/**
 * Created by user1 on 8/25/17.
 */
class SelectedContactsAdapter (private val list: ArrayList<ContactBase>, private val context: Context, private val listener: SelectedContactsListener): RecyclerView.Adapter<SelectedContactsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.selected_contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = list[position]

        holder!!.name.text = item.name
        holder.container.setOnClickListener {
            listener.onSelectedContactPressed(item)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }



    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        internal var container = itemView.findViewById<LinearLayout>(R.id.selectedContactItem)
        internal var name = itemView.findViewById<TextView>(R.id.contactName)
        internal var image = itemView.findViewById<ImageView>(R.id.contactImage)
    }

    interface SelectedContactsListener {
        fun onSelectedContactPressed(contact: ContactBase)
    }
}