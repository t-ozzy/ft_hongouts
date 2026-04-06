package com.tozeki.ft_hongouts

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyApplication : Application(), DefaultLifecycleObserver {

    override fun onCreate() {
        super<Application>.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    // アプリがフォアグラウンドになった時に呼ばれる
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("AppLifecycle", "アプリがフォアグラウンドに戻りました")

        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("should_show_toast", true)
            apply()
        }
    }

    // アプリがバックグラウンドになった時に呼ばれる
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("AppLifecycle", "アプリがバックグラウンドに移動しました")

        val sharedPref = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US)
        val currentDateTime = sdf.format(Date())
        with(sharedPref.edit()) {
            putString("last_timestamp", currentDateTime)
            apply()
        }
    }
}