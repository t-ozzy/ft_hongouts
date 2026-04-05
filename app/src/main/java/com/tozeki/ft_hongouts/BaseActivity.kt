package com.tozeki.ft_hongouts

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        showLastTimestamp()
    }

    override fun onPause() {
        super.onPause()
        saveCurrentTimestamp()
    }

    private fun saveCurrentTimestamp() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE) ?: return
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US)
        val currentDateTime = sdf.format(Date())
        with(sharedPref.edit()) {
            putString("last_timestamp", currentDateTime)
            apply()
        }
    }

    private fun showLastTimestamp() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE) ?: return
        val lastTimestamp = sharedPref.getString("last_timestamp", null)
        if (lastTimestamp != null) {
            Toast.makeText(this, "Last session: $lastTimestamp", Toast.LENGTH_LONG).show()
        }
    }
}
