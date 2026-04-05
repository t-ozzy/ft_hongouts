package com.tozeki.ft_hongouts

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.widget.Toolbar
import com.tozeki.ft_hongouts.data.Contact
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper

class MainActivity : BaseActivity() {

    private lateinit var listView: ListView
    private lateinit var addButton: Button

    private lateinit var repository: ContactRepository
    private var contacts: List<Contact> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Note: 共通化できそう
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Contacts"

        //Note: 共通化できそう
        repository = ContactRepository(DatabaseHelper(this))

        listView = findViewById(R.id.list_contacts)
        addButton = findViewById(R.id.button_add)

        listView.setOnItemClickListener { _, _, position, _ ->
            val contact = contacts[position]
            val intent = Intent(this, ContactDetailActivity::class.java)
            intent.putExtra("contact_id", contact.id)
            startActivity(intent)
        }

        addButton.setOnClickListener {
            val intent = Intent(this, ContactEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadContacts()
    }

    private fun loadContacts() {
        contacts = repository.getAllContacts()
        val items = contacts.map { "${it.lastName} ${it.firstName} (${it.phoneNumber})" }

        val adapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            items
        )
        listView.adapter = adapter
    }
}