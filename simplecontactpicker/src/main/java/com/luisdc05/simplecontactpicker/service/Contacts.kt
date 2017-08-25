package com.luisdc05.simplecontactpicker.service

import android.content.Context
import android.provider.ContactsContract
import com.luisdc05.simplecontactpicker.model.AndroidContact
import com.luisdc05.simplecontactpicker.model.ContactBase

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

        val result = ArrayList<ContactBase>()
        while (phones.moveToNext()) {
            val type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
            val typeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.resources, type, "").toString()
            val numberId = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
            val imageUri = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
            val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val rawPhoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            result.add(AndroidContact(rawPhoneNumber, name,typeLabel, imageUri))
        }
        phones.close()
        return result
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