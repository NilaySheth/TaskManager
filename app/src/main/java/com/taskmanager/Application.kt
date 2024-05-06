package com.taskmanager

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.taskmanager.data.local.prefs.UserPreferences
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        initSharedPrefs()
    }

    private fun initSharedPrefs() {
        UserPreferences.init(applicationContext)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}