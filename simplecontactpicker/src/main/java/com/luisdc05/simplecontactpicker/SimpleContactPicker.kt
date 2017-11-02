package com.luisdc05.simplecontactpicker

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.luisdc05.simplecontactpicker.adapter.ContactsAdapter
import com.luisdc05.simplecontactpicker.misc.ContactSelectionListener
import com.luisdc05.simplecontactpicker.misc.OnContactsReceived
import com.luisdc05.simplecontactpicker.misc.SoftKeyboard
import com.luisdc05.simplecontactpicker.model.ContactBase
import com.luisdc05.simplecontactpicker.service.Contacts
import java.text.Normalizer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by user1 on 8/25/17.
 */
class SimpleContactPicker : RecyclerView, ContactsAdapter.ContactsListener {

    private val contactsAdapter: ContactsAdapter by lazy { createAdapter() }

    private lateinit var pickedContactsView: PickedContacts

    var contacts = ArrayList<Pair<ContactBase, AtomicBoolean>>()
    var selectedContacts: ArrayList<ContactBase> = ArrayList()
        private set
        get() {
            return pickedContactsView.selectedContacts
        }
    private var filteredContacts = ArrayList<Pair<ContactBase, AtomicBoolean>>()
    var preselectedNumbers: Array<String>? = null
    var hidden: Array<String>? = null
    var hideKeyboardOnAction = true

    var selectionListener: ContactSelectionListener? = null

    private var filterCriteria = ""

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        initialize()
    }

    private fun initialize() {
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        layoutManager = llm
        adapter = contactsAdapter
        addOnScrollListener(ScrollListener())
    }

    private fun createAdapter(): ContactsAdapter {
        return ContactsAdapter(filteredContacts, context, this)
    }

    fun attachPickedContactsView(view: PickedContacts) {
        pickedContactsView = view
        pickedContactsView.contactPicker = this
    }

    private fun updateAdapter() {
        contactsAdapter.notifyDataSetChanged()
    }

    /**
     * Checks if the touched contact is selected
     */
    private fun checkIfSelected(contact: ContactBase): Boolean {
        return selectedContacts.any { it.numberOnly ==  contact.numberOnly}
    }

    /**
     * PreSelects the contacts if the preselected contacts array was provided
     */
    private fun preSelectContacts() {
        preselectedNumbers?.forEach { number ->
            for ((first, second) in contacts) {
                val numberOnly = first.numberOnly
                if (number == numberOnly) {
                    second.set(true)
                    pickedContactsView.addContact(first)
                    break
                }
            }
        }
    }

    private fun hideContacts() {
        hidden?.forEach { number ->
            val toDelete = contacts.firstOrNull { number == it.first.numberOnly }
            if (toDelete != null) contacts.remove(toDelete)
        }
    }

    /**
     * Filters the contacts with the given criteria if it matches the name or the number of the contact
     */
    fun filter(criteria: String) {
        filterCriteria = criteria
        val normalizedCriteria = Normalizer.
                normalize(criteria, Normalizer.Form.NFD).
                replace(Regex("\\p{InCOMBINING_DIACRITICAL_MARKS}+"), "").toLowerCase()

        filteredContacts.clear()

        if (normalizedCriteria == "") {
            filteredContacts.addAll(contacts)
        } else {
            contacts.forEach {
                if (it.first.searchName.contains(normalizedCriteria) || it.first.numberOnly.contains(normalizedCriteria)) {
                    filteredContacts.add(it)
                }
            }
        }

        contactsAdapter.notifyDataSetChanged()
    }

    /**
     * Loads the contacts into the view
     */
    fun loadContacts(listener: OnContactsReceived?) {
        ContactsTask(listener).execute()
    }

    fun updateContacts() {
        UpdateContactsTask().execute()
    }

    /**
     * Listener for when a contact is pressed
     */
    override fun onContactPressed(contact: ContactBase) {
        val removed = checkIfSelected(contact)
        if (removed) {
            pickedContactsView.removeContact(contact)
            deselectContact(contact)
        } else {
            if (selectionListener != null) {
                if (selectionListener?.beforeSelection(contact) == true) {
                    selectContact(contact)
                }
            } else {
                selectContact(contact)
            }
        }
        updateAdapter()
        hideKeyboard()
    }

    /**
     * Deselects a contact
     */
    internal fun deselectContact(contact: ContactBase) {
        updateContactList(contact, true)
        updateAdapter()
        hideKeyboard()
        selectionListener?.onContactDeselected(contact)
    }

    /**
     * Selects a contact
     */
    private fun selectContact(contact: ContactBase) {
        pickedContactsView.addContact(contact)
        updateContactList(contact, false)
        selectionListener?.onContactSelected(contact)
    }

    /**
     * Updates the contact and filtered contact lists
     */
    private fun updateContactList(contact: ContactBase, removed: Boolean) {
        val contactItem = contacts.first { it.first.numberOnly == contact.numberOnly }
        val filteredItem = filteredContacts.firstOrNull { it.first.numberOnly == contact.numberOnly }
        if (removed) {
            contactItem.second.set(false)
            filteredItem?.second?.set(false)
        } else {
            contactItem.second.set(true)
            filteredItem?.second?.set(true)
        }
    }

    private fun hideKeyboard() {
        if (hideKeyboardOnAction) {
            if (context is Activity) {
                SoftKeyboard.hideKeyboard(context as Activity)
            }
        }
    }

    private inner class ScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            hideKeyboard()
        }
    }


    private inner class ContactsTask(private val listener: OnContactsReceived?) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            val tempContacts = Contacts.getContacts(context)

            tempContacts.mapTo(contacts) {
                Pair(it, AtomicBoolean(false))
            }

            preSelectContacts()
            hideContacts()

            filteredContacts.addAll(contacts)
            return null
        }

        override fun onPostExecute(result: Void?) {
            listener?.onReceived(contacts.map { it.first })
        }
    }

    private inner class UpdateContactsTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            val tempContacts = Contacts.getContacts(context)
            removeContacts(tempContacts)
            updateOrAddContacts(tempContacts)
            removeSelectedContacts()
            Contacts.orderContacts(contacts)
            return null
        }

        override fun onPostExecute(result: Void?) {
            updateAdapter()
//            filter(searchInput.text.toString())
        }

        private fun removeContacts(tempContacts: ArrayList<ContactBase>) {
            contacts.removeAll { (first) -> tempContacts.none { it.numberOnly == first.numberOnly }}
        }

        private fun updateOrAddContacts(tempContacts: ArrayList<ContactBase>) {
            tempContacts.forEach { contact ->
                val index = contacts.indices.firstOrNull { contacts[it].first.numberOnly == contact.numberOnly }
                if (index != null) {
                    contacts[index] = Pair(contact, contacts[index].second)
                } else {
                    if (hidden != null) {
                        if (hidden?.none{ it == contact.numberOnly} == true) {
                            contacts.add(Pair(contact, AtomicBoolean(false)))
                        }
                    } else {
                        contacts.add(Pair(contact, AtomicBoolean(false)))
                    }
                }
            }
        }

        private fun removeSelectedContacts() {
            selectedContacts.forEach { selected ->
                if (contacts.none { selected.numberOnly == it.first.numberOnly }) {
                    pickedContactsView.removeContact(selected)
                }
            }
        }
    }
}