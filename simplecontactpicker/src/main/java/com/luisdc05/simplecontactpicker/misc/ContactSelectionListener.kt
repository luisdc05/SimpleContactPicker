package com.luisdc05.simplecontactpicker.misc

import com.luisdc05.simplecontactpicker.model.ContactBase

/**
 * Created by user1 on 9/21/17.
 */
interface ContactSelectionListener {
    // Returns a boolean to determine if the contact can be selected
    fun beforeSelection(contact: ContactBase): Boolean
    fun onContactSelected(contact: ContactBase)
    fun onContactDeselected(contact: ContactBase)
}