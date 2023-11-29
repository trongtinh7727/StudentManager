package com.app.studentmanagement.ui

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        val options = FirebaseOptions.Builder()
            .setProjectId("student-manager-97a58")
            .setApplicationId("1:267783932773:android:134542eea610c7e85aef8c")
            .setApiKey("AIzaSyCkjrOTiVO3HHYfv_VHrXHYr0XbDSaqgZM")
            .build()

        FirebaseApp.initializeApp(this, options, "secondary")
    }
}
