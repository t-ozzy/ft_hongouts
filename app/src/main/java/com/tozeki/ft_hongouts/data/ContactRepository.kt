package com.tozeki.ft_hongouts.data

import android.content.ContentValues
import android.database.Cursor

class ContactRepository(private val dbHelper: DatabaseHelper) {

    // 新規作成 (INSERT)
    fun insert(contact: Contact): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_FIRST_NAME, contact.firstName)
            put(DatabaseHelper.COLUMN_LAST_NAME, contact.lastName)
            put(DatabaseHelper.COLUMN_PHONE, contact.phoneNumber)
            put(DatabaseHelper.COLUMN_EMAIL, contact.email)
            put(DatabaseHelper.COLUMN_MEMO, contact.memo)
        }
        return db.insert(DatabaseHelper.TABLE_CONTACTS, null, values)
    }

    // 更新 (UPDATE)
    fun update(contact: Contact): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_FIRST_NAME, contact.firstName)
            put(DatabaseHelper.COLUMN_LAST_NAME, contact.lastName)
            put(DatabaseHelper.COLUMN_PHONE, contact.phoneNumber)
            put(DatabaseHelper.COLUMN_EMAIL, contact.email)
            put(DatabaseHelper.COLUMN_MEMO, contact.memo)
        }
        return db.update(
            DatabaseHelper.TABLE_CONTACTS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(contact.id.toString())
        )
    }

    // 削除 (DELETE)
    fun delete(contactId: Long): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            DatabaseHelper.TABLE_CONTACTS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(contactId.toString())
        )
    }

    // IDで1件取得 (SELECT)
    fun getContactById(contactId: Long): Contact? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CONTACTS,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(contactId.toString()),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                cursorToContact(it)
            } else {
                null
            }
        }
    }

    // 全件取得
    fun getAllContacts(): List<Contact> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CONTACTS,
            null, null, null, null, null,
            "${DatabaseHelper.COLUMN_LAST_NAME} ASC"
        )
        
		val contacts = mutableListOf<Contact>()
        cursor.use {
            while (it.moveToNext()) {
                contacts.add(cursorToContact(it))
            }
        }
        return contacts
    }

    // 電話番号で連絡先を検索
    fun getContactByPhoneNumber(phoneNumber: String): Contact? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_CONTACTS,
            null,
            "${DatabaseHelper.COLUMN_PHONE} = ?",
            arrayOf(phoneNumber),
            null,
            null,
            null
        )

        return cursor.use {
            if (it.moveToFirst()) {
                cursorToContact(it)
            } else {
                null
            }
        }
    }

    private fun cursorToContact(cursor: Cursor): Contact {
        return Contact(
            id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
            firstName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FIRST_NAME)),
            lastName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_LAST_NAME)),
            phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PHONE)),
            email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
            memo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMO))
        )
    }
}
