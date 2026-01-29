package com.example.passlessauthethication


import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PassLessApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}