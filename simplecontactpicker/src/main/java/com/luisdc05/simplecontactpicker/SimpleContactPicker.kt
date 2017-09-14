package com.luisdc05.simplecontactpicker

import android.content.Context
import android.os.AsyncTask
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.EditText
import android.widget.LinearLayout
import com.luisdc05.simplecontactpicker.adapter.ContactsAdapter
import com.luisdc05.simplecontactpicker.adapter.SelectedContactsAdapter
import com.luisdc05.simplecontactpicker.misc.OnContactsReceived
import com.luisdc05.simplecontactpicker.model.ContactBase
import com.luisdc05.simplecontactpicker.service.Contacts
import java.text.Normalizer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by user1 on 8/25/17.
 */
class SimpleContactPicker : LinearLayout, ContactsAdapter.ContactsListener, SelectedContactsAdapter.SelectedContactsListener {

    private var contactsAdapter: ContactsAdapter? = null
    private var selectedContactsAdapter: SelectedContactsAdapter? = null

    private var contacts = ArrayList<Pair<ContactBase, AtomicBoolean>>()
    var selectedContacts = ArrayList<ContactBase>()
        private set
    private var filteredContacts = ArrayList<Pair<ContactBase, AtomicBoolean>>()
    var preselectedNumbers: Array<String>? = null
    var hidden: Array<String>? = null

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var selectedContactsRecyclerView: RecyclerView
    private lateinit var searchInput: EditText

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        orientation = LinearLayout.VERTICAL

        LayoutInflater.from(context).inflate(R.layout.contact_picker, this, true)

        searchInput = getChildAt(1).findViewById(R.id.search)
        contactsRecyclerView = getChildAt(2) as RecyclerView
        selectedContactsRecyclerView = getChildAt(0) as RecyclerView
        setUpSearchInput()
    }

    /**
     * Sets up the search input
     */
    private fun setUpSearchInput() {
        showSearchIcon()
        searchInput.setOnTouchListener { view, motionEvent ->
            val DRAWABLE_RIGHT = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (searchInput.right - searchInput.compoundDrawables[DRAWABLE_RIGHT].getBounds().width())) {
                    searchInput.setText("")
                }
            }
            false
        }
        searchInput.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = searchInput.text.toString()
                if (text == "") {
                    showSearchIcon()
                } else {
                    showClearIcon()
                }
                filter(text)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    /**
     * Sets up the contacts recycler view
     */
    private fun setUpContactsRecyclerView() {
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        contactsRecyclerView.layoutManager = llm
        contactsAdapter = ContactsAdapter(filteredContacts, context, this)
        contactsRecyclerView.adapter = contactsAdapter!!
    }

    /**
     * Set ups the selected contacts recycler view
     */
    private fun setUpSelectedContactsRecyclerView() {
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        selectedContactsRecyclerView.layoutManager = llm
        selectedContactsAdapter = SelectedContactsAdapter(selectedContacts, context, this)
        selectedContactsRecyclerView.adapter = selectedContactsAdapter!!
    }

    /**
     * Shows the clear icon in the search input
     */
    private fun showClearIcon() {
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete, 0)
    }

    /**
     * Shows the search icon in the search input
     */
    private fun showSearchIcon() {
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
    }

    /**
     * Updates the adapters
     */
    private fun updateAdapters(contact: ContactBase, removed: Boolean) {
        val item = filteredContacts.firstOrNull { it.first.numberOnly == contact.numberOnly }
        if (removed) {
            contacts.first { it.first.numberOnly == contact.numberOnly}.second.set(false)
            item?.second?.set(false)
        } else {
            contacts.first { it.first.numberOnly == contact.numberOnly }.second.set(true)
            item?.second?.set(true)
        }

        updateAdapters()
        if (!removed) {
            selectedContactsRecyclerView.scrollToPosition(selectedContacts.size - 1)
        }
    }

    private fun updateAdapters() {
        contactsAdapter!!.notifyDataSetChanged()
        selectedContactsAdapter!!.notifyDataSetChanged()
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
                    selectedContacts.add(first)
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
    private fun filter(criteria: String) {
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

        contactsAdapter?.notifyDataSetChanged()
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
        if (removed) { // remove if it is selected
            selectedContacts.remove(contact)
        } else { // add if not
            selectedContacts.add(contact)
        }
        updateAdapters(contact, removed)
    }

    /**
     * Listener for when a selected contract is pressed
     */
    override fun onSelectedContactPressed(contact: ContactBase) {
        selectedContacts.remove(contact) // always remove
        updateAdapters(contact, true)
    }

    inner class ContactsTask(private val listener: OnContactsReceived?) : AsyncTask<Void, Void, Void>() {
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
            setUpContactsRecyclerView()
            setUpSelectedContactsRecyclerView()
            listener?.onReceived(contacts.map { it.first })
        }
    }

    inner class UpdateContactsTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg p0: Void?): Void? {
            val tempContacts = Contacts.getContacts(context)
            removeContacts(tempContacts)
            updateOrAddContacts(tempContacts)
            removeSelectedContacts()
            Contacts.orderContacts(contacts)
            return null
        }

        override fun onPostExecute(result: Void?) {
            updateAdapters()
            filter(searchInput.text.toString())
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
                    contacts.add(Pair(contact, AtomicBoolean(false)))
                }
            }
        }

        private fun removeSelectedContacts() {
            selectedContacts.forEach { selected ->
                if (contacts.none { selected.numberOnly == it.first.numberOnly }) {
                    selectedContacts.remove(selected)
                }
            }
        }
    }
}