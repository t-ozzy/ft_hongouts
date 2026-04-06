package com.tozeki.ft_hongouts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.tozeki.ft_hongouts.data.ChatMessage
import com.tozeki.ft_hongouts.data.ChatRepository
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
            
            // onReceiveごとにインスタンス化するのがBroadcastReceiverの定石
            val dbHelper = DatabaseHelper(context)
            val contactRepo = ContactRepository(dbHelper)
            val chatRepo = ChatRepository(dbHelper)

            for (message in messages) {
                val senderAddress = message.displayOriginatingAddress
                if (senderAddress == null) {
                    Log.d("SmsReceiver", "Sender address is null")
                    continue
                }

                // 送信者の電話番号から連絡先を検索
                val contact = contactRepo.getContactByPhoneNumber(senderAddress)
                if (contact == null) {
                    Log.d("SmsReceiver", "Contact not found for sender: $senderAddress")
                    continue
                }
                
                val messageBody = message.displayMessageBody
                val timestamp = message.timestampMillis
                val contactId = contact.id

                val chatMessage = ChatMessage(
                    contactId = contactId,
                    isSent = false,
                    message = messageBody,
                    timestamp = timestamp
                )
                chatRepo.insert(chatMessage)
            }
        }
    }
}
