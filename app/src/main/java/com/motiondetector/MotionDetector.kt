package com.motiondetector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.MediaPlayer
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.abs

class MotionDetector(private val context: Context) : ImageAnalysis.Analyzer {
    private var previousBitmap: Bitmap? = null
    private var lastDetectionTime = 0L
    private var mediaPlayer: MediaPlayer? = null
    
    // Default threshold values
    var motionSizeThreshold = 0.05f  // 0.0 to 1.0, percentage of changed pixels
    var motionSpeedThreshold = 500L  // milliseconds between detections
    
    // Optional ROI (Region of Interest)
    private var roi: Rect? = null
    
    // Motion detection state
    private var isMotionDetected = false
    
    override fun analyze(imageProxy: ImageProxy) {
        val currentBitmap = imageProxyToBitmap(imageProxy)
        
        if (currentBitmap != null) {
            if (previousBitmap != null) {
                // Detect motion by comparing current frame with previous frame
                val motionDetected = detectMotion(previousBitmap!!, currentBitmap)
                
                val currentTime = System.currentTimeMillis()
                if (motionDetected && currentTime - lastDetectionTime > motionSpeedThreshold) {
                    Log.d(TAG, "Motion detected")
                    lastDetectionTime = currentTime
                    isMotionDetected = true
                    
                    // Play sound on motion detection
                    playNotificationSound()
                } else if (!motionDetected) {
                    isMotionDetected = false
                }
            }
            
            // Update previous bitmap
            previousBitmap?.recycle()
            previousBitmap = currentBitmap
        }
        
        imageProxy.close()
    }
    
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer
        
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        
        val nv21 = ByteArray(ySize + uSize + vSize)
        
        // U and V are swapped
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)
        
        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, image.width, image.height), 100, out)
        val imageBytes = out.toByteArray()
        
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
    
    private fun detectMotion(prevBitmap: Bitmap, currBitmap: Bitmap): Boolean {
        // Resize bitmaps for faster processing if needed
        val width = prevBitmap.width
        val height = prevBitmap.height
        
        // Create scaled bitmaps for faster processing
        val scaleFactor = 0.25f
        val scaledWidth = (width * scaleFactor).toInt()
        val scaledHeight = (height * scaleFactor).toInt()
        
        val scaledPrev = Bitmap.createScaledBitmap(prevBitmap, scaledWidth, scaledHeight, false)
        val scaledCurr = Bitmap.createScaledBitmap(currBitmap, scaledWidth, scaledHeight, false)
        
        // Get pixels from bitmaps
        val prevPixels = IntArray(scaledWidth * scaledHeight)
        val currPixels = IntArray(scaledWidth * scaledHeight)
        
        scaledPrev.getPixels(prevPixels, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight)
        scaledCurr.getPixels(currPixels, 0, scaledWidth, 0, 0, scaledWidth, scaledHeight)
        
        // Calculate differences
        var changedPixels = 0
        val pixelThreshold = 30 // RGB difference threshold to consider a pixel changed
        
        for (i in prevPixels.indices) {
            // Extract RGB components
            val prevColor = prevPixels[i]
            val currColor = currPixels[i]
            
            val prevR = (prevColor shr 16) and 0xFF
            val prevG = (prevColor shr 8) and 0xFF
            val prevB = prevColor and 0xFF
            
            val currR = (currColor shr 16) and 0xFF
            val currG = (currColor shr 8) and 0xFF
            val currB = currColor and 0xFF
            
            // Calculate difference
            val diffR = abs(prevR - currR)
            val diffG = abs(prevG - currG)
            val diffB = abs(prevB - currB)
            
            // If the difference is significant, count as changed pixel
            if (diffR > pixelThreshold || diffG > pixelThreshold || diffB > pixelThreshold) {
                changedPixels++
            }
        }
        
        // Calculate percentage of changed pixels
        val totalPixels = prevPixels.size
        val changePercentage = changedPixels.toFloat() / totalPixels
        
        // Check if ROI is defined and apply it
        if (roi != null) {
            // In a real implementation, we would only count changes within the ROI
            // For simplicity, we're just using the whole frame for now
        }
        
        // Clean up
        scaledPrev.recycle()
        scaledCurr.recycle()
        
        Log.d(TAG, "Change percentage: $changePercentage, threshold: $motionSizeThreshold")
        
        // Return true if the percentage of changed pixels exceeds the threshold
        return changePercentage > motionSizeThreshold
    }
    
    private fun playNotificationSound() {
        try {
            if (mediaPlayer == null) {
                // Use a default system sound if the custom sound is not available
                try {
                    mediaPlayer = MediaPlayer.create(context, R.raw.notification_sound)
                } catch (e: Exception) {
                    // Fallback to system notification sound
                    mediaPlayer = MediaPlayer.create(context, android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                }
                
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
    
    fun isMotionDetected(): Boolean {
        return isMotionDetected
    }
    
    companion object {
        private const val TAG = "MotionDetector"
    }
}
