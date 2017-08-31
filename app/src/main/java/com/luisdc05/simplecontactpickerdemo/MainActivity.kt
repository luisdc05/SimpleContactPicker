package com.luisdc05.simplecontactpickerdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.luisdc05.simplecontactpicker.SimpleContactPicker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val contactPicker = findViewById<SimpleContactPicker>(R.id.picker)
        val selected = arrayOf("6641195415")
        val hidden = arrayOf("6643683773")
        contactPicker.preselectedNumbers = selected
        contactPicker.hidden = hidden
        contactPicker.loadContacts()

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            Log.d("MAIN", contactPicker.selectedContacts.size.toString())
        }
    }
}
