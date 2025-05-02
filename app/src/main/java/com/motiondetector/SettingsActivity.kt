package com.motiondetector

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.motiondetector.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Set up the toolbar with back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        
        // Get the MotionDetector instance from the application
        val motionDetector = (application as? MotionDetectorApplication)?.motionDetector
        
        // Set up the size threshold seekbar
        binding.sizeThresholdSeekBar.progress = (motionDetector?.motionSizeThreshold ?: 0.5f * 100).toInt()
        binding.sizeThresholdValue.text = "${binding.sizeThresholdSeekBar.progress}%"
        
        binding.sizeThresholdSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.sizeThresholdValue.text = "$progress%"
                motionDetector?.motionSizeThreshold = progress / 100f
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Set up the speed threshold seekbar
        val speedProgress = convertSpeedToProgress(motionDetector?.motionSpeedThreshold ?: 500L)
        binding.speedThresholdSeekBar.progress = speedProgress
        binding.speedThresholdValue.text = "${convertProgressToSpeed(speedProgress)} ms"
        
        binding.speedThresholdSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val speedValue = convertProgressToSpeed(progress)
                binding.speedThresholdValue.text = "$speedValue ms"
                motionDetector?.motionSpeedThreshold = speedValue.toLong()
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        
        // Set up the ROI switch
        binding.roiSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.drawRoiButton.isEnabled = isChecked
            if (!isChecked) {
                motionDetector?.setRegionOfInterest(null)
            }
        }
        
        // Set up the draw ROI button
        binding.drawRoiButton.setOnClickListener {
            // This will be implemented in a later step
            // It will open a dialog or activity to draw the ROI
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    // Convert speed threshold (ms) to progress (0-100)
    private fun convertSpeedToProgress(speed: Long): Int {
        // Map 100ms-2000ms to 0-100 progress
        return ((speed - 100) / 19).toInt().coerceIn(0, 100)
    }
    
    // Convert progress (0-100) to speed threshold (ms)
    private fun convertProgressToSpeed(progress: Int): Int {
        // Map 0-100 progress to 100ms-2000ms
        return (progress * 19 + 100).coerceIn(100, 2000)
    }
}
