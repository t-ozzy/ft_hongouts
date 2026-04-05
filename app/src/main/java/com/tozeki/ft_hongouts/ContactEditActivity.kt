package com.tozeki.ft_hongouts

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.tozeki.ft_hongouts.data.Contact
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper

class ContactEditActivity : BaseActivity() {

    private lateinit var lastNameEdit: EditText
    private lateinit var firstNameEdit: EditText
    private lateinit var phoneEdit: EditText
    private lateinit var emailEdit: EditText
    private lateinit var memoEdit: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private lateinit var repository: ContactRepository
    private var contactId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_edit)

        repository = ContactRepository(DatabaseHelper(this))
        contactId = intent.getLongExtra("contact_id", -1)

        lastNameEdit = findViewById(R.id.edit_last_name)
        firstNameEdit = findViewById(R.id.edit_first_name)
        phoneEdit = findViewById(R.id.edit_phone)
        emailEdit = findViewById(R.id.edit_email)
        memoEdit = findViewById(R.id.edit_memo)
        saveButton = findViewById(R.id.button_save)
        cancelButton = findViewById(R.id.button_cancel)

        saveButton.setOnClickListener { onSaveClicked() }
        cancelButton.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (contactId != -1L) {
            loadContact()
        }
    }

    private fun loadContact() {
        val contact = repository.getContactById(contactId)
        if (contact != null) {
            lastNameEdit.setText(contact.lastName)
            firstNameEdit.setText(contact.firstName)
            phoneEdit.setText(contact.phoneNumber)
            emailEdit.setText(contact.email)
            memoEdit.setText(contact.memo)
        }
    }

    private fun onSaveClicked() {
        val lastName = lastNameEdit.text.toString().trim()
        val firstName = firstNameEdit.text.toString().trim()
        val phone = phoneEdit.text.toString().trim()
        val email = emailEdit.text.toString().trim()
        val memo = memoEdit.text.toString().trim()

        if (lastName.isEmpty() || firstName.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Required fields are empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (contactId == -1L) {
            val newContact = Contact(
                id = 0,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phone,
                email = email,
                memo = memo
            )
            repository.insert(newContact)
        } else {
            val updatedContact = Contact(
                id = contactId,
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phone,
                email = email,
                memo = memo
            )
            repository.update(updatedContact)
        }

        setResult(RESULT_OK)
        finish()
    }
}
