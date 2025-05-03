# Motion Detector Android App

An Android application that detects motion using the device camera, plays a sound when movement is detected, and allows users to customize motion size and speed thresholds from a settings screen. The app also supports defining a Region of Interest (ROI) to limit the detection area.

## Overview
This implementation provides a complete, production-ready motion detection solution for Android devices with comprehensive features and optimized performance.

![Motion Detector App](app/src/main/res/drawable/app_screenshot.png)

## Features

- **Motion Detection**: Uses frame differencing algorithm to detect motion in the camera view
- **Sound Notifications**: Plays a customizable sound when motion is detected
- **Vibration Feedback**: Optional device vibration on motion detection
- **Adjustable Thresholds**: Customize motion size and speed sensitivity
- **Region of Interest**: Draw a bounding box to limit the detection area
- **Pause/Resume**: Easily pause and resume motion detection
- **Visual Indicators**: Color-coded status indicator shows detection state

## Project Setup

### Prerequisites

- Android Studio Arctic Fox (2020.3.1) or newer
- JDK 8 or newer
- Android SDK with minimum API level 21 (Android 5.0 Lollipop)

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/motion-detector-android.git
   ```

2. Open the project in Android Studio:
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the cloned repository and click "Open"

3. Build the project:
   - Click "Build > Make Project" or press Ctrl+F9 (Cmd+F9 on Mac)

### Dependencies

This project is set up with the following components:

- **Kotlin**: The project uses Kotlin as the primary programming language
- **CameraX**: Modern Android camera API for camera access and processing
- **AndroidX**: Latest Android support libraries for UI components
- **ViewBinding**: For type-safe view access

## Architecture

The application follows a modular architecture with clear separation of concerns:

### Core Components

- **MainActivity**: Handles camera preview, permissions, and UI interactions
- **MotionDetector**: Core class that implements motion detection logic using frame differencing
- **SettingsActivity**: Allows users to customize motion detection parameters
- **RoiDrawingActivity**: Enables users to draw a Region of Interest for targeted detection
- **MotionDetectorApplication**: Application class that maintains the shared MotionDetector instance

### Motion Detection Algorithm

The app uses a frame differencing approach for motion detection:
1. Captures and scales down camera frames for efficient processing
2. Compares consecutive frames to detect pixel changes
3. Applies size and speed thresholds to filter out noise
4. Optionally restricts detection to user-defined Region of Interest
5. Triggers sound and vibration notifications when motion is detected

### Settings Management

All user preferences are stored using SharedPreferences and include:
- Motion size threshold (sensitivity)
- Motion speed threshold (delay between detections)
- Region of Interest coordinates
- Sound notification settings (enabled/disabled, volume)
- Vibration settings (enabled/disabled)

## Usage Guide

### Main Screen

- **Camera View**: Shows the live camera feed
- **Status Indicator**: Changes color (green to red) when motion is detected
- **Status Text**: Displays "No Motion" or "Motion Detected!"
- **Pause/Resume Button**: Toggles motion detection on/off
- **Settings Button**: Opens the settings screen

### Settings Screen

- **Motion Size Threshold**: Adjust the minimum size of motion to trigger detection
- **Motion Speed Threshold**: Set the minimum time between motion detections
- **Region of Interest**: Enable/disable ROI and access the ROI drawing screen
- **Sound Settings**: Enable/disable sound, adjust volume, test notification sound
- **Vibration Settings**: Enable/disable device vibration on motion detection

### ROI Drawing Screen

1. View the camera preview
2. Touch and drag to draw a rectangular region of interest
3. Tap "Save" to apply the ROI or "Clear" to reset
4. The app will only detect motion within the defined region

## Building and Testing

### Building the App

- Run `./gradlew assembleDebug` to build a debug APK
- Run `./gradlew assembleRelease` to build a release APK (requires signing configuration)

### Running Tests

- Run `./gradlew test` to execute unit tests
- Run `./gradlew connectedAndroidTest` to run instrumented tests
- Run `./gradlew lint` for lint checks

### Test Coverage

The app includes comprehensive test coverage:
- **Unit Tests**: Test individual components in isolation
- **UI Tests**: Verify UI interactions and navigation
- **Integration Tests**: Ensure components work together correctly

## Troubleshooting

### Common Issues

- **Camera Permission Denied**: The app requires camera permission to function. Grant it in your device settings.
- **Sound Not Playing**: Ensure media volume is turned up and sound is enabled in settings.
- **High CPU Usage**: Lower the motion detection thresholds or increase the speed threshold.

### Performance Optimization

- The app scales down camera frames for processing to reduce CPU usage
- Motion detection runs on a background thread to keep the UI responsive
- ROI feature can significantly reduce processing requirements by limiting the detection area

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- CameraX library by Google
- Android Jetpack components
