package com.tozeki.ft_hongouts

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        applyThemeColor()
    }

    override fun onResume() {
        super.onResume()
        showLastTimestamp()
    }

    private fun showLastTimestamp() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE) ?: return
        val shouldShow = sharedPref.getBoolean("should_show_toast", false)
        val lastTimestamp = sharedPref.getString("last_timestamp", null)

        if (shouldShow && lastTimestamp != null) {
            Toast.makeText(this, "Last session: $lastTimestamp", Toast.LENGTH_LONG).show()
            with(sharedPref.edit()) {
                putBoolean("should_show_toast", false)
                apply()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

	// setSupportActionBarで登録したToolbarのメニューアイテムが選択された時の処理
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.color_default -> {
                saveThemeColor("#6200EE")
                true
            }
            R.id.color_red -> {
                saveThemeColor("#FF0000")
                true
            }
            R.id.color_green -> {
                saveThemeColor("#00FF00")
                true
            }
            R.id.color_blue -> {
                saveThemeColor("#0000FF")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveThemeColor(colorHex: String) {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        with(editor) {
            putString("theme_color", colorHex)
            apply()
        }
        applyThemeColor()
    }

    protected fun applyThemeColor() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val colorHex = sharedPref.getString("theme_color", "#6200EE") // Default
        val color = Color.parseColor(colorHex)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar?.setBackgroundColor(color)
    }
}
