package com.motiondetector

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.motiondetector.databinding.ActivityRoiDrawingBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class RoiDrawingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRoiDrawingBinding
    private lateinit var cameraExecutor: ExecutorService
    
    // ROI drawing variables
    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f
    private var isDrawing = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoiDrawingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar with back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        // Set up camera preview
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
        
        // Set up ROI drawing overlay
        binding.roiOverlay.setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true
        }
        
        // Set up save button
        binding.saveButton.setOnClickListener {
            saveRoi()
        }
        
        // Set up clear button
        binding.clearButton.setOnClickListener {
            clearRoi()
        }
    }
    
    private fun handleTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                endX = event.x
                endY = event.y
                isDrawing = true
                binding.roiOverlay.invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                binding.roiOverlay.invalidate()
            }
            MotionEvent.ACTION_UP -> {
                endX = event.x
                endY = event.y
                isDrawing = false
                binding.roiOverlay.invalidate()
                
                // Enable save button if a valid ROI is drawn
                binding.saveButton.isEnabled = isValidRoi()
            }
        }
    }
    
    private fun isValidRoi(): Boolean {
        val width = Math.abs(endX - startX)
        val height = Math.abs(endY - startY)
        
        // ROI must be at least 50x50 pixels
        return width >= 50 && height >= 50
    }
    
    private fun saveRoi() {
        // Convert ROI coordinates to normalized values (0.0 to 1.0)
        val viewWidth = binding.roiOverlay.width.toFloat()
        val viewHeight = binding.roiOverlay.height.toFloat()
        
        val left = Math.min(startX, endX) / viewWidth
        val top = Math.min(startY, endY) / viewHeight
        val right = Math.max(startX, endX) / viewWidth
        val bottom = Math.max(startY, endY) / viewHeight
        
        // Save normalized ROI to shared preferences
        val prefs = getSharedPreferences("MotionDetectorPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putFloat("roi_left", left)
            putFloat("roi_top", top)
            putFloat("roi_right", right)
            putFloat("roi_bottom", bottom)
            putBoolean("roi_enabled", true)
            apply()
        }
        
        // Update MotionDetector with new ROI
        val motionDetector = (application as? MotionDetectorApplication)?.motionDetector
        
        // Convert normalized coordinates to actual pixel values for the camera preview
        val previewWidth = binding.viewFinder.width
        val previewHeight = binding.viewFinder.height
        
        val roiRect = Rect(
            (left * previewWidth).toInt(),
            (top * previewHeight).toInt(),
            (right * previewWidth).toInt(),
            (bottom * previewHeight).toInt()
        )
        
        motionDetector?.setRegionOfInterest(roiRect)
        
        Toast.makeText(this, getString(R.string.roi_saved), Toast.LENGTH_SHORT).show()
        finish()
    }
    
    private fun clearRoi() {
        startX = 0f
        startY = 0f
        endX = 0f
        endY = 0f
        binding.roiOverlay.invalidate()
        binding.saveButton.isEnabled = false
        
        // Clear ROI in shared preferences
        val prefs = getSharedPreferences("MotionDetectorPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            remove("roi_left")
            remove("roi_top")
            remove("roi_right")
            remove("roi_bottom")
            putBoolean("roi_enabled", false)
            apply()
        }
        
        // Clear ROI in MotionDetector
        val motionDetector = (application as? MotionDetectorApplication)?.motionDetector
        motionDetector?.setRegionOfInterest(null)
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            
            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            
            // Select back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview
                )
                
            } catch (exc: Exception) {
                Toast.makeText(
                    this,
                    "Failed to start camera: ${exc.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    
    // Custom view for drawing ROI overlay
    class RoiOverlayView(context: Context) : View(context) {
        private val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }
        
        var startX = 0f
        var startY = 0f
        var endX = 0f
        var endY = 0f
        var isDrawing = false
        
        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            
            if (startX != endX && startY != endY) {
                val left = Math.min(startX, endX)
                val top = Math.min(startY, endY)
                val right = Math.max(startX, endX)
                val bottom = Math.max(startY, endY)
                
                canvas.drawRect(left, top, right, bottom, paint)
            }
        }
        
        fun updateRoi(startX: Float, startY: Float, endX: Float, endY: Float, isDrawing: Boolean) {
            this.startX = startX
            this.startY = startY
            this.endX = endX
            this.endY = endY
            this.isDrawing = isDrawing
            invalidate()
        }
    }
    
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RoiDrawingActivity::class.java)
        }
    }
}
