package com.motiondetector

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SettingsActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SettingsActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testMotionSizeThresholdSeekBarIsDisplayed() {
        onView(withId(R.id.sizeThresholdSeekBar))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.sizeThresholdValue))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testMotionSpeedThresholdSeekBarIsDisplayed() {
        onView(withId(R.id.speedThresholdSeekBar))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.speedThresholdValue))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRoiSwitchIsDisplayed() {
        onView(withId(R.id.roiSwitch))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDrawRoiButtonIsDisplayed() {
        onView(withId(R.id.drawRoiButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSoundEnabledSwitchIsDisplayed() {
        onView(withId(R.id.soundEnabledSwitch))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSoundVolumeSeekBarIsDisplayed() {
        onView(withId(R.id.soundVolumeSeekBar))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.soundVolumeValue))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testVibrationEnabledSwitchIsDisplayed() {
        onView(withId(R.id.vibrationEnabledSwitch))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testTestSoundButtonIsDisplayed() {
        onView(withId(R.id.testSoundButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDrawRoiButtonOpensRoiDrawingActivity() {
        // Enable ROI switch first (since the button is disabled by default)
        onView(withId(R.id.roiSwitch))
            .perform(click())
        
        // Click on the draw ROI button
        onView(withId(R.id.drawRoiButton))
            .perform(click())
        
        // Verify that RoiDrawingActivity is launched
        intended(hasComponent(RoiDrawingActivity::class.java.name))
    }
}
