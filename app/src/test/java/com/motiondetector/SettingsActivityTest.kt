package com.motiondetector

import android.content.Context
import android.content.SharedPreferences
import android.widget.SeekBar
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.motiondetector.databinding.ActivitySettingsBinding
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SettingsActivityTest {

    @Mock
    private lateinit var motionDetector: MotionDetector
    
    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    
    @Mock
    private lateinit var editor: SharedPreferences.Editor
    
    private lateinit var context: Context
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()
        
        // Mock shared preferences
        `when`(sharedPreferences.edit()).thenReturn(editor)
        `when`(editor.putBoolean(anyString(), anyBoolean())).thenReturn(editor)
        `when`(editor.putFloat(anyString(), anyFloat())).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.putLong(anyString(), anyLong())).thenReturn(editor)
        
        // Mock application
        val application = mock(MotionDetectorApplication::class.java)
        `when`(application.motionDetector).thenReturn(motionDetector)
    }
    
    @Test
    fun `test motion size threshold seekbar updates motion detector`() {
        // Launch the activity
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Get the binding
                val bindingField = SettingsActivity::class.java.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity) as ActivitySettingsBinding
                
                // Simulate seekbar change
                val listener = binding.sizeThresholdSeekBar.onSeekBarChangeListener
                listener.onProgressChanged(binding.sizeThresholdSeekBar, 75, true)
                
                // Verify that the motion detector was updated
                verify(motionDetector).motionSizeThreshold = 0.75f
                
                // Verify that the text view was updated
                assert(binding.sizeThresholdValue.text == "75%")
            }
        }
    }
    
    @Test
    fun `test motion speed threshold seekbar updates motion detector`() {
        // Launch the activity
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Get the binding
                val bindingField = SettingsActivity::class.java.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity) as ActivitySettingsBinding
                
                // Simulate seekbar change
                val listener = binding.speedThresholdSeekBar.onSeekBarChangeListener
                listener.onProgressChanged(binding.speedThresholdSeekBar, 50, true)
                
                // Calculate expected speed value
                val expectedSpeed = (50 * 19 + 100).coerceIn(100, 2000)
                
                // Verify that the motion detector was updated
                verify(motionDetector).motionSpeedThreshold = expectedSpeed.toLong()
                
                // Verify that the text view was updated
                assert(binding.speedThresholdValue.text == "$expectedSpeed ms")
            }
        }
    }
    
    @Test
    fun `test sound enabled switch updates motion detector`() {
        // Launch the activity
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Get the binding
                val bindingField = SettingsActivity::class.java.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity) as ActivitySettingsBinding
                
                // Simulate switch change
                binding.soundEnabledSwitch.isChecked = false
                binding.soundEnabledSwitch.callOnClick()
                
                // Verify that the motion detector was updated
                verify(motionDetector).setSoundEnabled(false)
            }
        }
    }
    
    @Test
    fun `test sound volume seekbar updates motion detector`() {
        // Launch the activity
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Get the binding
                val bindingField = SettingsActivity::class.java.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity) as ActivitySettingsBinding
                
                // Simulate seekbar change
                val listener = binding.soundVolumeSeekBar.onSeekBarChangeListener
                listener.onProgressChanged(binding.soundVolumeSeekBar, 60, true)
                
                // Verify that the motion detector was updated
                verify(motionDetector).setSoundVolume(0.6f)
                
                // Verify that the text view was updated
                assert(binding.soundVolumeValue.text == "60%")
            }
        }
    }
    
    @Test
    fun `test vibration enabled switch updates motion detector`() {
        // Launch the activity
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Get the binding
                val bindingField = SettingsActivity::class.java.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity) as ActivitySettingsBinding
                
                // Simulate switch change
                binding.vibrationEnabledSwitch.isChecked = false
                binding.vibrationEnabledSwitch.callOnClick()
                
                // Verify that the motion detector was updated
                verify(motionDetector).setVibrationEnabled(false)
            }
        }
    }
    
    @Test
    fun `test test sound button triggers notification`() {
        // Launch the activity
        ActivityScenario.launch(SettingsActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                // Get the binding
                val bindingField = SettingsActivity::class.java.getDeclaredField("binding")
                bindingField.isAccessible = true
                val binding = bindingField.get(activity) as ActivitySettingsBinding
                
                // Simulate button click
                binding.testSoundButton.performClick()
                
                // Verify that the motion detector was updated
                verify(motionDetector).testNotification()
                
                // Verify that a toast was shown
                assert(ShadowToast.getTextOfLatestToast().contains("Testing notification"))
            }
        }
    }
}
