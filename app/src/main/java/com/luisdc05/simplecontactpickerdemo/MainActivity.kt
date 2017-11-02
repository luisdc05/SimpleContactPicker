package com.luisdc05.simplecontactpickerdemo

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import com.luisdc05.simplecontactpicker.PickedContacts
import com.luisdc05.simplecontactpicker.SimpleContactPicker
import com.luisdc05.simplecontactpicker.misc.ContactSelectionListener
import com.luisdc05.simplecontactpicker.misc.OnContactsReceived
import com.luisdc05.simplecontactpicker.model.ContactBase

class MainActivity : AppCompatActivity(), OnContactsReceived, ContactSelectionListener {
    val TAG = "MAIN"

    private lateinit var contactPicker: SimpleContactPicker
    private lateinit var pickedContacts: PickedContacts
    private lateinit var searchInput: TextInputEditText

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactPicker = findViewById(R.id.picker)
        pickedContacts = findViewById(R.id.picked)
        val selected = arrayOf("3214567845")
        val hidden = arrayOf("2148794513")
        contactPicker.attachPickedContactsView(pickedContacts)
        contactPicker.preselectedNumbers = selected
        contactPicker.hidden = hidden
        contactPicker.loadContacts(this)
        contactPicker.selectionListener = this

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            Log.d(TAG, contactPicker.selectedContacts.size.toString())
        }

        searchInput = findViewById(R.id.search)
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
                contactPicker.filter(text)
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onResume() {
        super.onResume()

        contactPicker.updateContacts()
    }

    override fun onReceived(contacts: List<ContactBase>) {
        // Do something when the contacts are loaded
        Log.d(TAG, contactPicker.contacts.size.toString())
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
        return true
    }

    private fun showClearIcon() {
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_delete, 0)
    }

    private fun showSearchIcon() {
        searchInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_search, 0)
    }
}
