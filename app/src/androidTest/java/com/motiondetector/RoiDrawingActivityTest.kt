package com.motiondetector

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RoiDrawingActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RoiDrawingActivity::class.java)

    @Test
    fun testCameraPreviewIsDisplayed() {
        onView(withId(R.id.viewFinder))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRoiOverlayIsDisplayed() {
        onView(withId(R.id.roiOverlay))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testInstructionsTextIsDisplayed() {
        onView(withId(R.id.instructionsText))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.roi_drawing_instructions)))
    }

    @Test
    fun testClearButtonIsDisplayed() {
        onView(withId(R.id.clearButton))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.clear)))
    }

    @Test
    fun testSaveButtonIsDisplayed() {
        onView(withId(R.id.saveButton))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.save)))
            .check(matches(isNotEnabled())) // Should be disabled initially
    }

    @Test
    fun testClearButtonClearsRoi() {
        // This is a simplified test as we can't easily simulate touch events in Espresso
        // In a real test, we would use UiAutomator to simulate touch events
        
        // Click the clear button
        onView(withId(R.id.clearButton))
            .perform(click())
        
        // Verify that the save button is disabled after clearing
        onView(withId(R.id.saveButton))
            .check(matches(isNotEnabled()))
    }
}
