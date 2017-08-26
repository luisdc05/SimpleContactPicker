package com.luisdc05.simplecontactpicker

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.luisdc05.simplecontactpicker.adapter.ContactsAdapter
import com.luisdc05.simplecontactpicker.adapter.SelectedContactsAdapter
import com.luisdc05.simplecontactpicker.model.ContactBase
import com.luisdc05.simplecontactpicker.service.Contacts
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by user1 on 8/25/17.
 */
class SimpleContactPicker : LinearLayout, ContactsAdapter.ContactsListener, SelectedContactsAdapter.SelectedContactsListener {

    private var contactsAdapter: ContactsAdapter? = null
    private var selectedContactsAdapter: SelectedContactsAdapter? = null

    private var contacts = ArrayList<Pair<ContactBase, AtomicBoolean>>()
    private var selectedContacts = ArrayList<ContactBase>()
    private var filteredContacts = ArrayList<Pair<ContactBase, AtomicBoolean>>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        orientation = LinearLayout.VERTICAL

        LayoutInflater.from(context).inflate(R.layout.contact_picker, this, true)

        val tempContacts = Contacts.getContacts(context)
        tempContacts.mapTo(contacts) {
            Pair(it, AtomicBoolean(false))
        }

        filteredContacts.addAll(contacts)

        contactsAdapter = ContactsAdapter(filteredContacts, context, this)
        setUpContactsRecyclerView(getChildAt(2) as RecyclerView)
        selectedContactsAdapter = SelectedContactsAdapter(selectedContacts, context, this)
        setUpSelectedContactsRecyclerView(getChildAt(0) as RecyclerView)
    }

    private fun setUpContactsRecyclerView(view: RecyclerView) {
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        view.layoutManager = llm
        view.adapter = contactsAdapter!!
    }

    private fun setUpSelectedContactsRecyclerView(view: RecyclerView) {
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        view.layoutManager = llm
        view.adapter = selectedContactsAdapter!!
    }

    override fun onContactPressed(contact: ContactBase) {
        val removed = checkIfSelected(contact)
        if (removed) { // remove if it is selected
            selectedContacts.remove(contact)
        } else { // add if not
            selectedContacts.add(contact)
        }
        updateAdapters(contact, removed)
    }

    override fun onSelectedContactPressed(contact: ContactBase) {
        selectedContacts.remove(contact) // always remove
        updateAdapters(contact, true)
    }

    fun getSelectedContacts(): ArrayList<ContactBase> {
        return selectedContacts
    }

    private fun updateAdapters(contact: ContactBase, removed: Boolean) {
        val item = filteredContacts.firstOrNull { it.first.number == contact.number }
        if (removed) {
            contacts.first { it.first.number == contact.number }.second.set(false)
            item?.second?.set(false)
        } else {
            contacts.first { it.first.number == contact.number }.second.set(true)
            item?.second?.set(true)
        }

        contactsAdapter!!.notifyDataSetChanged()
        selectedContactsAdapter!!.notifyDataSetChanged()
    }

    private fun checkIfSelected(contact: ContactBase): Boolean {
        return selectedContacts.any { it.number ==  contact.number}
    }
}