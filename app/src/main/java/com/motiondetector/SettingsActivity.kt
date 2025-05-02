package com.motiondetector

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.motiondetector.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var soundEnabled = true
    private var soundVolume = 0.8f // 0.0 to 1.0
    private var vibrationEnabled = true
    
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
            // Launch the ROI drawing activity
            val intent = RoiDrawingActivity.newIntent(this)
            startActivity(intent)
        }
        
        // Set up sound settings
        
        // Load saved sound settings
        loadSoundSettings()
        
        // Initialize UI with saved settings
        binding.soundEnabledSwitch.isChecked = soundEnabled
        binding.soundVolumeSeekBar.progress = (soundVolume * 100).toInt()
        binding.soundVolumeValue.text = "${binding.soundVolumeSeekBar.progress}%"
        binding.vibrationEnabledSwitch.isChecked = vibrationEnabled
        
        // Update MotionDetector with current settings
        motionDetector?.setSoundEnabled(soundEnabled)
        motionDetector?.setSoundVolume(soundVolume)
        motionDetector?.setVibrationEnabled(vibrationEnabled)
        
        // Set up sound enabled switch
        binding.soundEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            soundEnabled = isChecked
            motionDetector?.setSoundEnabled(isChecked)
            binding.soundVolumeSeekBar.isEnabled = isChecked
            saveSoundSettings()
        }
        
        // Set up sound volume seekbar
        binding.soundVolumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.soundVolumeValue.text = "$progress%"
                soundVolume = progress / 100f
                motionDetector?.setSoundVolume(soundVolume)
            }
            
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                saveSoundSettings()
            }
        })
        
        // Set up vibration enabled switch
        binding.vibrationEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            vibrationEnabled = isChecked
            motionDetector?.setVibrationEnabled(isChecked)
            saveSoundSettings()
        }
        
        // Set up test sound button
        binding.testSoundButton.setOnClickListener {
            motionDetector?.testNotification()
            Toast.makeText(this, "Testing notification", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadSoundSettings() {
        val prefs = getSharedPreferences("MotionDetectorPrefs", Context.MODE_PRIVATE)
        soundEnabled = prefs.getBoolean("sound_enabled", true)
        soundVolume = prefs.getFloat("sound_volume", 0.8f)
        vibrationEnabled = prefs.getBoolean("vibration_enabled", true)
    }
    
    private fun saveSoundSettings() {
        val prefs = getSharedPreferences("MotionDetectorPrefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("sound_enabled", soundEnabled)
            putFloat("sound_volume", soundVolume)
            putBoolean("vibration_enabled", vibrationEnabled)
            apply()
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
