package com.luisdc05.simplecontactpicker.misc

import com.luisdc05.simplecontactpicker.model.ContactBase
import java.util.*

/**
 * Created by user1 on 8/25/17.
 */
interface OnContactsReceived {
    fun onReceived(contacts: ArrayList<ContactBase>)
}