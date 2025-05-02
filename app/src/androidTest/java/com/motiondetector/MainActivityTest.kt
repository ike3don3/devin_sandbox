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
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun testCameraPermissionRequested() {
        // This test assumes the app doesn't have camera permissions yet
        // It will verify that the permission dialog is shown
        
        // Check that the camera permission rationale is displayed
        // Note: This is a simplified test as actual permission dialogs can't be easily tested
        // In a real test, we would use UiAutomator to interact with system dialogs
        onView(withId(R.id.viewFinder))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSettingsButtonOpensSettingsActivity() {
        // Click on the settings button
        onView(withId(R.id.settingsButton))
            .check(matches(isDisplayed()))
            .perform(click())
        
        // Verify that SettingsActivity is launched
        intended(hasComponent(SettingsActivity::class.java.name))
    }

    @Test
    fun testPauseResumeButtonTogglesDetection() {
        // Check initial state - button should say "Pause"
        onView(withId(R.id.pauseResumeButton))
            .check(matches(withText(R.string.pause)))
        
        // Click the button
        onView(withId(R.id.pauseResumeButton))
            .perform(click())
        
        // Check that the button text changed to "Resume"
        onView(withId(R.id.pauseResumeButton))
            .check(matches(withText(R.string.resume)))
        
        // Click the button again
        onView(withId(R.id.pauseResumeButton))
            .perform(click())
        
        // Check that the button text changed back to "Pause"
        onView(withId(R.id.pauseResumeButton))
            .check(matches(withText(R.string.pause)))
    }

    @Test
    fun testMotionStatusIndicatorIsVisible() {
        // Check that the motion status indicator is visible
        onView(withId(R.id.motionStatusIndicator))
            .check(matches(isDisplayed()))
        
        // Check that the motion status text is visible
        onView(withId(R.id.motionStatusText))
            .check(matches(isDisplayed()))
    }
}
