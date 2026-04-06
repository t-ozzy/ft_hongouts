package com.tozeki.ft_hongouts.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "contacts.db"
        private const val DATABASE_VERSION = 2 // バージョンを2に上げる

        // テーブル名とカラム名の定義
        const val TABLE_CONTACTS = "contacts"
        const val COLUMN_ID = "_id"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"
        const val COLUMN_PHONE = "phone_number"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_MEMO = "memo"

        // 会話履歴テーブルの定義
        const val TABLE_MESSAGES = "messages"
        const val COLUMN_MSG_ID = "_id"
        const val COLUMN_MSG_CONTACT_ID = "contact_id"
        const val COLUMN_MSG_IS_SENT = "is_sent"
        const val COLUMN_MSG_CONTENT = "content"
        const val COLUMN_MSG_TIMESTAMP = "timestamp"

        // テーブル作成クエリ
        private const val TABLE_CREATE = """
            CREATE TABLE $TABLE_CONTACTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FIRST_NAME TEXT NOT NULL,
                $COLUMN_LAST_NAME TEXT NOT NULL,
                $COLUMN_PHONE TEXT NOT NULL,
                $COLUMN_EMAIL TEXT,
                $COLUMN_MEMO TEXT
            );
        """

        private const val TABLE_MESSAGES_CREATE = """
            CREATE TABLE $TABLE_MESSAGES (
                $COLUMN_MSG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_MSG_CONTACT_ID INTEGER NOT NULL,
                $COLUMN_MSG_IS_SENT INTEGER NOT NULL, -- 1: 送信, 0: 受信
                $COLUMN_MSG_CONTENT TEXT NOT NULL,
                $COLUMN_MSG_TIMESTAMP INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_MSG_CONTACT_ID) REFERENCES $TABLE_CONTACTS($COLUMN_ID) ON DELETE CASCADE
            );
        """
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(TABLE_CREATE)
        db.execSQL(TABLE_MESSAGES_CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(TABLE_MESSAGES_CREATE)
        }
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        if (!db.isReadOnly) {
            // 外部キー制約を有効にする
            db.execSQL("PRAGMA foreign_keys=ON;")
        }
    }
}
