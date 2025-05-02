# Motion Detector Android App

An Android application that detects motion using the device camera, plays a sound when movement is detected, and allows users to customize motion size and speed thresholds from a settings screen.

## Project Setup

This project is set up with the following components:

- **Kotlin**: The project uses Kotlin as the primary programming language.
- **ML Kit**: Google's ML Kit is used for motion detection through image labeling.
- **CameraX**: Modern Android camera API for camera access and processing.
- **AndroidX**: Latest Android support libraries for UI components.

## Development Environment

- Android Studio
- Minimum SDK: 21 (Android 5.0 Lollipop)
- Target SDK: 33 (Android 13)

## Project Structure

- `MainActivity`: Handles camera preview and integration with motion detection.
- `MotionDetector`: Core class that implements motion detection logic using ML Kit.
- `SettingsActivity`: Allows users to customize motion detection parameters.
- `MotionDetectorApplication`: Application class that maintains the shared MotionDetector instance.

## Building and Testing

- Run `./gradlew test` to execute unit tests.
- Run `./gradlew lint` for lint checks.
