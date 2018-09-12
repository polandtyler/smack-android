package com.tylerpoland.smack_android.Controller

import android.app.Application
import com.tylerpoland.smack_android.Utils.SharedPrefs

class App: Application() {

    companion object {
        lateinit var sharedPreferences: SharedPrefs
    }

    override fun onCreate() {
        sharedPreferences = SharedPrefs(applicationContext)
        super.onCreate()
    }

}