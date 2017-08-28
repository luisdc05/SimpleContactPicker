package com.luisdc05.simplecontactpicker.service

import android.content.Context
import android.provider.ContactsContract
import com.luisdc05.simplecontactpicker.model.AndroidContact
import com.luisdc05.simplecontactpicker.model.ContactBase
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by user1 on 8/25/17.
 */
object Contacts {

    /**
     * Gets the contacts from the device
     */
    fun getContacts(context: Context): ArrayList<ContactBase> {
        val phones = context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        )

        var result = ArrayList<ContactBase>()
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
        result = removeDuplicates(result)
        orderByName(result)
        return result
    }

    private fun removeDuplicates(contacts: ArrayList<ContactBase>): ArrayList<ContactBase> {
        val newList = ArrayList<ContactBase>()

        contacts.forEach { contact ->
            if (!newList.any { it.numberOnly ==  contact.numberOnly }) {
                newList.add(contact)
            }
        }

        return newList
    }

    /**
     * Orders the contacts by name
     */
    private fun orderByName(contacts: ArrayList<ContactBase>) {
        Collections.sort(contacts) { contact, t1 ->
            contact.name.compareTo(t1.name)
        }
    }

    /**
     * Projection for the device contact query
     */
    private val projection: Array<String>
        get() = arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                ContactsContract.Contacts.PHOTO_URI,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        )
}