package com.luisdc05.simplecontactpicker

import android.app.Activity
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.luisdc05.simplecontactpicker.adapter.SelectedContactsAdapter
import com.luisdc05.simplecontactpicker.misc.SoftKeyboard
import com.luisdc05.simplecontactpicker.model.ContactBase

/**
 * Created by user1 on 11/2/17.
 */
class PickedContacts: RecyclerView, SelectedContactsAdapter.SelectedContactsListener {

    private val selectedContactsAdapter: SelectedContactsAdapter by lazy { createAdapter() }

    internal var selectedContacts = ArrayList<ContactBase>()
        private set

    internal lateinit var contactPicker: SimpleContactPicker

    constructor(context: Context): super(context)

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        initialize()
    }

    override fun onSelectedContactPressed(contact: ContactBase) {
        removeContact(contact)
        contactPicker.deselectContact(contact)
        hideKeyboard()
    }

    private fun initialize() {
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager = llm
        adapter = selectedContactsAdapter
    }

    private fun createAdapter(): SelectedContactsAdapter {
        return SelectedContactsAdapter(selectedContacts, context, this)
    }

    private fun hideKeyboard() {
        if (contactPicker.hideKeyboardOnAction) {
            if (context is AppCompatActivity) {
                SoftKeyboard.hideKeyboard(context as AppCompatActivity)
            } else if (context is Activity) {
                SoftKeyboard.hideKeyboard(context as Activity)
            }
        }
    }

    internal fun addContact(contact: ContactBase) {
        selectedContacts.add(contact)
        adapter.notifyDataSetChanged()
        scrollToPosition(selectedContacts.size - 1)
    }

    internal fun removeContact(contact: ContactBase) {
        selectedContacts.removeAll { it.numberOnly == contact.numberOnly }
        adapter.notifyDataSetChanged()
    }
}