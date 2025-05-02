package com.motiondetector

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.motiondetector.databinding.ActivityMainBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var motionDetector: MotionDetector
    private val handler = Handler(Looper.getMainLooper())
    private var isDetectionActive = true

    // Motion status check runnable
    private val motionStatusChecker = object : Runnable {
        override fun run() {
            if (::motionDetector.isInitialized && isDetectionActive) {
                updateMotionStatus(motionDetector.isMotionDetected())
            }
            handler.postDelayed(this, MOTION_CHECK_INTERVAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for settings button
        binding.settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Initialize motion detector
        motionDetector = (application as MotionDetectorApplication).motionDetector

        // Set up the executor for camera operations
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Set up motion detection status indicator
        binding.motionStatusIndicator.visibility = View.VISIBLE
        updateMotionStatus(false)

        // Set up pause/resume button
        binding.pauseResumeButton.setOnClickListener {
            toggleDetection()
        }

        // Start motion status checker
        handler.post(motionStatusChecker)
    }

    private fun toggleDetection() {
        isDetectionActive = !isDetectionActive
        binding.pauseResumeButton.text = if (isDetectionActive) getString(R.string.pause) else getString(R.string.resume)
        binding.motionStatusIndicator.visibility = if (isDetectionActive) View.VISIBLE else View.INVISIBLE
        
        if (!isDetectionActive) {
            // Reset motion status when paused
            updateMotionStatus(false)
        }
    }

    private fun updateMotionStatus(motionDetected: Boolean) {
        binding.motionStatusIndicator.setBackgroundColor(
            if (motionDetected) Color.RED else Color.GREEN
        )
        binding.motionStatusText.text = getString(
            if (motionDetected) R.string.motion_detected else R.string.no_motion
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // Image analysis for motion detection
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, motionDetector)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

                Toast.makeText(this, getString(R.string.camera_started), Toast.LENGTH_SHORT).show()

            } catch (exc: Exception) {
                Toast.makeText(this, "Failed to start camera: ${exc.message}", 
                    Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume motion status checker
        handler.post(motionStatusChecker)
    }

    override fun onPause() {
        super.onPause()
        // Remove motion status checker callbacks
        handler.removeCallbacks(motionStatusChecker)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove motion status checker callbacks
        handler.removeCallbacks(motionStatusChecker)
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val MOTION_CHECK_INTERVAL = 100L // milliseconds
    }
}
