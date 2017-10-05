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
import com.luisdc05.simplecontactpicker.misc.ImageLoader
import com.luisdc05.simplecontactpicker.model.ContactBase
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by user1 on 8/25/17.
 */
class ContactsAdapter(private val list: ArrayList<Pair<ContactBase, AtomicBoolean>>, private val context: Context, private val listener: ContactsListener):RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.contact_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val item = list[position]

        holder!!.name.text = item.first.name
        holder.number.text = item.first.mobileNumber
        holder.type.text = item.first.numberType
        if (item.second.get()) {
            holder.selected.visibility = View.VISIBLE
        } else {
            holder.selected.visibility = View.INVISIBLE
        }

        holder.container.setOnClickListener {
            listener.onContactPressed(item.first)
        }

        loadImage(item.first.imagePath, holder.avatar)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun loadImage(path: String?, view: ImageView) {
        if (path != null) {
            ImageLoader.loadImagePath(context, path, R.drawable.avatar_placeholder, view)
            return
        }

        ImageLoader.loadDrawable(context, R.drawable.avatar_placeholder, view)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        internal var container = itemView.findViewById<LinearLayout>(R.id.contactItem)
        internal var name = itemView.findViewById<TextView>(R.id.contactName)
        internal var number = itemView.findViewById<TextView>(R.id.contactNumber)
        internal var type = itemView.findViewById<TextView>(R.id.contactNumberType)
        internal var avatar = itemView.findViewById<ImageView>(R.id.contactImage)
        internal var selected = itemView.findViewById<ImageView>(R.id.contactSelected)
    }

    interface ContactsListener {
        fun onContactPressed(contact: ContactBase)
    }
}