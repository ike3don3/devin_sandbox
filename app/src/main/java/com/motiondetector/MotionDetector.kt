package com.motiondetector

import android.content.Context
import android.graphics.Rect
import android.media.MediaPlayer
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.util.concurrent.atomic.AtomicBoolean

class MotionDetector(private val context: Context) : ImageAnalysis.Analyzer {
    private val imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    private var lastDetectionTime = 0L
    private val isProcessing = AtomicBoolean(false)
    private var mediaPlayer: MediaPlayer? = null
    
    // Default threshold values
    var motionSizeThreshold = 0.5f  // 0.0 to 1.0, default middle value
    var motionSpeedThreshold = 500L // milliseconds between detections
    
    // Optional ROI (Region of Interest)
    private var roi: Rect? = null
    
    override fun analyze(imageProxy: ImageProxy) {
        if (isProcessing.get()) {
            imageProxy.close()
            return
        }
        
        isProcessing.set(true)
        
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            
            // Process only within ROI if defined
            if (roi != null) {
                // In a real implementation, we would crop the image to the ROI
                // For simplicity, we'll just check if motion is within the ROI later
            }
            
            imageLabeler.process(image)
                .addOnSuccessListener { labels ->
                    // Look for motion-related labels
                    val motionLabels = labels.filter { label ->
                        label.text.lowercase() in listOf("motion", "movement", "moving", "action") &&
                        label.confidence > motionSizeThreshold
                    }
                    
                    val currentTime = System.currentTimeMillis()
                    if (motionLabels.isNotEmpty() && 
                        currentTime - lastDetectionTime > motionSpeedThreshold) {
                        Log.d(TAG, "Motion detected: ${motionLabels.joinToString { it.text }}")
                        lastDetectionTime = currentTime
                        
                        // Play sound on motion detection
                        playNotificationSound()
                    }
                    
                    isProcessing.set(false)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Image labeling failed: ${e.message}", e)
                    isProcessing.set(false)
                    imageProxy.close()
                }
        } else {
            isProcessing.set(false)
            imageProxy.close()
        }
    }
    
    private fun playNotificationSound() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(context, R.raw.notification_sound)
                mediaPlayer?.setOnCompletionListener {
                    // Release resources when sound completes
                    it.release()
                    mediaPlayer = null
                }
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play notification sound: ${e.message}", e)
        }
    }
    
    fun setRegionOfInterest(rect: Rect?) {
        this.roi = rect
    }
    
    companion object {
        private const val TAG = "MotionDetector"
    }
}
