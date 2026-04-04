package com.tozeki.ft_hongouts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper

class ContactDetailActivity : AppCompatActivity() {

    private lateinit var nameText: TextView
    private lateinit var phoneText: TextView
    private lateinit var emailText: TextView
    private lateinit var memoText: TextView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button

    private lateinit var repository: ContactRepository
    private var contactId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        repository = ContactRepository(DatabaseHelper(this))
        contactId = intent.getLongExtra("contact_id", -1)

        if (contactId == -1L) {
            Toast.makeText(this, "Error: Contact not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        nameText = findViewById(R.id.text_detail_name)
        phoneText = findViewById(R.id.text_detail_phone)
        emailText = findViewById(R.id.text_detail_email)
        memoText = findViewById(R.id.text_detail_memo)
        editButton = findViewById(R.id.button_edit)
        deleteButton = findViewById(R.id.button_delete)
        backButton = findViewById(R.id.button_back)

        editButton.setOnClickListener {
            val intent = Intent(this, ContactEditActivity::class.java)
            intent.putExtra("contact_id", contactId)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            repository.delete(contactId)
            Toast.makeText(this, "Contact deleted", Toast.LENGTH_SHORT).show()
            finish()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadContactDetails()
    }

    private fun loadContactDetails() {
        val contact = repository.getContactById(contactId)
        if (contact != null) {
            nameText.text = "${contact.lastName} ${contact.firstName}"
            phoneText.text = "Phone: ${contact.phoneNumber}"
            emailText.text = "Email: ${contact.email}"
			memoText.text = "Memo: ${contact.memo}"
        } else {
            finish()
        }
    }
}
