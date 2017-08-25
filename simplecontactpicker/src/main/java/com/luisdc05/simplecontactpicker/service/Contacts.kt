package com.luisdc05.simplecontactpicker.service

import android.content.Context
import android.provider.ContactsContract
import com.luisdc05.simplecontactpicker.model.ContactBase
import java.util.*

/**
 * Created by user1 on 8/25/17.
 */
object Contacts {

    fun getContacts(context: Context): ArrayList<ContactBase> {
        val phones = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        )
        phones.close()
        return  deviceContacts
    }

    private val deviceContacts: ArrayList<ContactBase>
        get() {

            return ArrayList()
        }

    private val projection: Array<String>
        get() = arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        )
}