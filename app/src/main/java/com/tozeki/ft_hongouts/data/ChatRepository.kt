package com.tozeki.ft_hongouts.data

import android.content.ContentValues
import android.database.Cursor

class ChatRepository(private val dbHelper: DatabaseHelper) {

    // メッセージの保存 (INSERT)
    fun insert(message: ChatMessage): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_MSG_CONTACT_ID, message.contactId)
            put(DatabaseHelper.COLUMN_MSG_IS_SENT, if (message.isSent) 1 else 0)
            put(DatabaseHelper.COLUMN_MSG_CONTENT, message.message)
            put(DatabaseHelper.COLUMN_MSG_TIMESTAMP, message.timestamp)
        }
        return db.insert(DatabaseHelper.TABLE_MESSAGES, null, values)
    }

    // 連絡先ごとのメッセージ取得 (SELECT)
    fun getMessagesForContact(contactId: Long): List<ChatMessage> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_MESSAGES,
            null,
            "${DatabaseHelper.COLUMN_MSG_CONTACT_ID} = ?",
            arrayOf(contactId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_MSG_TIMESTAMP} ASC"
        )

        val messages = mutableListOf<ChatMessage>()
        cursor.use {
            while (it.moveToNext()) {
                messages.add(cursorToChatMessage(it))
            }
        }
        return messages
    }

    private fun cursorToChatMessage(cursor: Cursor): ChatMessage {
        return ChatMessage(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MSG_ID)),
            contactId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MSG_CONTACT_ID)),
            isSent = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MSG_IS_SENT)) == 1,
            message = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MSG_CONTENT)),
            timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MSG_TIMESTAMP))
        )
    }
}
