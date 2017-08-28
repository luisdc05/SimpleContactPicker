package com.luisdc05.simplecontactpicker.model

import java.text.Normalizer

/**
 * Created by user1 on 8/25/17.
 */
abstract class ContactBase constructor(val mobileNumber: String, val name: String, val numberType: String, val imagePath: String?) {
    var searchName: String
        private set

    init {
        val regex = Regex("\\p{InCOMBINING_DIACRITICAL_MARKS}+")
        searchName = Normalizer.normalize(name, Normalizer.Form.NFD).replace(regex, "").toLowerCase()
    }

    val numberOnly: String
        get() = mobileNumber.replace(Regex("\\D+"), "")


}