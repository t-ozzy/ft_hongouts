package com.tozeki.ft_hongouts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tozeki.ft_hongouts.data.ChatMessage
import com.tozeki.ft_hongouts.data.ChatRepository
import com.tozeki.ft_hongouts.data.Contact
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper
import android.util.Log

class ContactDetailActivity : BaseActivity() {

    private lateinit var nameText: TextView
    private lateinit var phoneText: TextView
    private lateinit var emailText: TextView
    private lateinit var memoText: TextView
    private lateinit var editSmsContent: EditText
    private lateinit var sendSmsButton: Button
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var backButton: Button

    private lateinit var repository: ContactRepository
    private lateinit var chatRepository: ChatRepository
    
    private var contactId: Long = -1
    private var currentContact: Contact? = null

    companion object {
        private const val PERMISSIONS_REQUEST_SEND_SMS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Contact Details"
        toolbar.setNavigationOnClickListener { finish() }

        repository = ContactRepository(DatabaseHelper(this))
        chatRepository = ChatRepository(DatabaseHelper(this))
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
        editSmsContent = findViewById(R.id.edit_sms_content)
        sendSmsButton = findViewById(R.id.button_send_sms)
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

        sendSmsButton.setOnClickListener {
            checkSmsPermissionAndSend()
        }
    }

    override fun onResume() {
        super.onResume()
        val contact = repository.getContactById(contactId) // ローカル変数に代入
        currentContact = contact
        if (contact != null) {
            nameText.text = "${contact.lastName} ${contact.firstName}"
            phoneText.text = "Phone: ${contact.phoneNumber}"
            emailText.text = "Email: ${contact.email}"
            memoText.text = "Memo: ${contact.memo}"
        } else {
            finish()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_SEND_SMS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                sendSms()
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkSmsPermissionAndSend() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), PERMISSIONS_REQUEST_SEND_SMS)
        } else {
            sendSms()
        }
    }

    private fun sendSms() {
        val message = editSmsContent.text.toString()
        val phoneNumber = currentContact?.phoneNumber

        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            Toast.makeText(this, "Phone number is missing", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val smsManager: SmsManager = this.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            
            // Save to DB
            val chatMessage = ChatMessage(
                contactId = contactId,
                isSent = true,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            chatRepository.insert(chatMessage)

            Toast.makeText(this, "SMS sent", Toast.LENGTH_SHORT).show()
            editSmsContent.text.clear()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("ContactDetail", "Failed to send SMS", e)
        }
    }

    
}
