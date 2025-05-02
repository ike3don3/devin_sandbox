package com.motiondetector

import android.app.Application

class MotionDetectorApplication : Application() {
    // Create a single instance of MotionDetector to be used throughout the app
    val motionDetector by lazy { MotionDetector(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any application-wide configurations here
    }
}
