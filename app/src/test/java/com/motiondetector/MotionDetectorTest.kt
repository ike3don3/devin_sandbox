package com.motiondetector

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.media.MediaPlayer
import android.os.Vibrator
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class MotionDetectorTest {

    @Mock
    private lateinit var context: Context
    
    @Mock
    private lateinit var mediaPlayer: MediaPlayer
    
    @Mock
    private lateinit var vibrator: Vibrator
    
    private lateinit var motionDetector: MotionDetector
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
        // Mock system service for vibrator
        `when`(context.getSystemService(Context.VIBRATOR_SERVICE)).thenReturn(vibrator)
        
        // Create the motion detector with mocked context
        motionDetector = MotionDetector(context)
        
        // Use reflection to set the mediaPlayer field
        val mediaPlayerField = MotionDetector::class.java.getDeclaredField("mediaPlayer")
        mediaPlayerField.isAccessible = true
        mediaPlayerField.set(motionDetector, mediaPlayer)
    }
    
    @Test
    fun `test motion size threshold setting`() {
        // Default value should be 0.05f
        assert(motionDetector.motionSizeThreshold == 0.05f)
        
        // Set a new value
        motionDetector.motionSizeThreshold = 0.1f
        
        // Check that the value was updated
        assert(motionDetector.motionSizeThreshold == 0.1f)
    }
    
    @Test
    fun `test motion speed threshold setting`() {
        // Default value should be 500L
        assert(motionDetector.motionSpeedThreshold == 500L)
        
        // Set a new value
        motionDetector.motionSpeedThreshold = 1000L
        
        // Check that the value was updated
        assert(motionDetector.motionSpeedThreshold == 1000L)
    }
    
    @Test
    fun `test sound enabled setting`() {
        // Enable sound
        motionDetector.setSoundEnabled(true)
        
        // Trigger a notification
        motionDetector.testNotification()
        
        // Verify that the media player was started
        verify(mediaPlayer).start()
    }
    
    @Test
    fun `test sound disabled setting`() {
        // Disable sound
        motionDetector.setSoundEnabled(false)
        
        // Trigger a notification
        motionDetector.testNotification()
        
        // Verify that the media player was not started
        verify(mediaPlayer, never()).start()
    }
    
    @Test
    fun `test sound volume setting`() {
        // Set volume to 0.5f
        motionDetector.setSoundVolume(0.5f)
        
        // Trigger a notification
        motionDetector.testNotification()
        
        // Verify that the volume was set on the media player
        verify(mediaPlayer).setVolume(0.5f, 0.5f)
    }
    
    @Test
    fun `test vibration enabled setting`() {
        // Enable vibration
        motionDetector.setVibrationEnabled(true)
        
        // Trigger a notification
        motionDetector.testNotification()
        
        // Verify that the vibrator was used
        verify(vibrator).vibrate(anyLong())
    }
    
    @Test
    fun `test vibration disabled setting`() {
        // Disable vibration
        motionDetector.setVibrationEnabled(false)
        
        // Trigger a notification
        motionDetector.testNotification()
        
        // Verify that the vibrator was not used
        verify(vibrator, never()).vibrate(anyLong())
    }
    
    @Test
    fun `test region of interest setting`() {
        // Create a test ROI
        val roi = Rect(10, 20, 100, 200)
        
        // Set the ROI
        motionDetector.setRegionOfInterest(roi)
        
        // Use reflection to check that the ROI was set
        val roiField = MotionDetector::class.java.getDeclaredField("roi")
        roiField.isAccessible = true
        val actualRoi = roiField.get(motionDetector) as Rect
        
        // Verify that the ROI was set correctly
        assert(actualRoi.left == 10)
        assert(actualRoi.top == 20)
        assert(actualRoi.right == 100)
        assert(actualRoi.bottom == 200)
    }
    
    @Test
    fun `test clear region of interest`() {
        // First set an ROI
        val roi = Rect(10, 20, 100, 200)
        motionDetector.setRegionOfInterest(roi)
        
        // Then clear it
        motionDetector.setRegionOfInterest(null)
        
        // Use reflection to check that the ROI was cleared
        val roiField = MotionDetector::class.java.getDeclaredField("roi")
        roiField.isAccessible = true
        val actualRoi = roiField.get(motionDetector)
        
        // Verify that the ROI was cleared
        assert(actualRoi == null)
    }
}
