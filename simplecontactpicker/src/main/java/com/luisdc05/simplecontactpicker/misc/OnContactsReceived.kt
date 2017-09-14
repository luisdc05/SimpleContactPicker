package com.luisdc05.simplecontactpicker.misc

import com.luisdc05.simplecontactpicker.model.ContactBase

/**
 * Created by user1 on 8/25/17.
 */
interface OnContactsReceived {
    fun onReceived(contacts: List<ContactBase>)
}