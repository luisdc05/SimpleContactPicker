package com.luisdc05.simplecontactpickerdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.luisdc05.simplecontactpicker.SimpleContactPicker
import com.luisdc05.simplecontactpicker.misc.ContactSelectionListener
import com.luisdc05.simplecontactpicker.misc.OnContactsReceived
import com.luisdc05.simplecontactpicker.model.ContactBase

class MainActivity : AppCompatActivity(), OnContactsReceived, ContactSelectionListener {
    val TAG = "MAIN"

    private lateinit var contactPicker: SimpleContactPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactPicker = findViewById(R.id.picker)
        val selected = arrayOf("3214567845")
        val hidden = arrayOf("2148794513")
        contactPicker.preselectedNumbers = selected
        contactPicker.hidden = hidden
        contactPicker.loadContacts(this)
        contactPicker.selectionListener = this

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            Log.d(TAG, contactPicker.selectedContacts.size.toString())
        }
    }

    override fun onResume() {
        super.onResume()

        contactPicker.updateContacts()
    }

    override fun onReceived(contacts: List<ContactBase>) {
        // Do something when the contacts are loaded
    }

    override fun onContactSelected(contact: ContactBase) {
        // Do something when a contact is selected
        Log.d(TAG, "A contact has been selected")
    }

    override fun onContactDeselected(contact: ContactBase) {
        // Do something when a contact is deselected
        Log.d(TAG, "A contact has been deselected")
    }

    override fun beforeSelection(contact: ContactBase): Boolean {
        return false
    }
}
