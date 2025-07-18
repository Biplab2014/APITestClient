# ADB Toolbox 🔧

**A comprehensive Android Debug Bridge (ADB) management application built with Jetpack Compose and Material 3.**

## 🚀 Overview

ADB Toolbox is a powerful Android application that provides a user-friendly interface for managing ADB connections, monitoring device logs, and performing various ADB operations. Built with modern Android development practices using Jetpack Compose, MVVM architecture, Hilt dependency injection, and Material 3 design system.

## ✨ Features

### 🔌 Device Management
- **Wireless ADB Connection**: Connect to devices over Wi-Fi using IP address and port
- **Device Discovery**: Automatic detection and listing of connected ADB devices
- **Connection Status**: Real-time monitoring of device connection states
- **Device Information**: Detailed device specs including Android version, API level, model, and manufacturer

### 📱 Device Operations
- **APK Management**: Install and manage APK files on connected devices
- **App Management**: View and manage installed applications
- **ADB Shell**: Execute shell commands directly on connected devices
- **Device Info**: Comprehensive device information and specifications

### 📊 Logcat Monitoring
- **Real-time Logs**: Stream live logcat output from connected devices
- **Log Filtering**: Filter logs by level (Verbose, Debug, Info, Warn, Error, Fatal)
- **Log History**: View historical log entries with timestamps
- **Multiple Formats**: Support for different logcat output formats

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Hilt
- **Navigation**: Navigation Compose
- **State Management**: StateFlow & Compose State
- **Coroutines**: For asynchronous operations
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)

## 🛠️ Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 24+
- Kotlin 1.9+
- ADB is installed and accessible in the system PATH

### Build Instructions
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AdbToolbox
   ```

2. **Open in Android Studio**
   - Import the project
   - Sync Gradle files
   - Ensure ADB is properly configured

3. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

## 📱 Usage Guide

### Connecting to Devices
1. **Enable Developer Options** on target device
2. **Enable USB Debugging** and **Wireless ADB** (Android 11+)
3. In ADB Toolbox, tap **"Connect Device"**
4. Enter device IP address and port (default: 5555)
5. Tap **"Connect"** to establish connection

### Managing Connected Devices
- View all connected devices in the main screen
- Tap on a device to view detailed information
- Use **"Disconnect"** to remove device connection
- **"Refresh"** to update the device list and information

### Logcat Monitoring
1. Select a connected device
2. Navigate to **"Logcat Viewer"**
3. Choose log level filters
4. Start/stop log streaming
5. View real-time logs with timestamps and tags

### APK and App Management
- **APK Manager**: Install APK files to connected devices
- **App Manager**: View and manage installed applications
- **Device Info**: Access comprehensive device specifications

## 🔧 Core Components

### AdbService
The central service handles all ADB operations:



## 🎨 UI Components

### Key Screens
- **HomeScreen**: Main dashboard with device list and navigation
- **LogcatScreen**: Real-time log monitoring interface
- **ApkManagerScreen**: APK installation and management
- **AppManagerScreen**: Installed app management
- **ShellScreen**: ADB shell command interface
- **DeviceInfoScreen**: Detailed device information

### Design Features
- **Material 3 Design**: Modern, accessible interface with dynamic theming
- **Dark/Light Theme**: Automatic theme switching based on system preferences
- **Responsive Layout**: Optimised for different screen sizes and orientations
- **Real-time Updates**: Live data streaming with proper state management
- **Error Handling**: Comprehensive error states and user feedback

## 🔒 Permissions

ADB Toolbox requires minimal permissions:

- `INTERNET` - For network-based ADB connections
- `ACCESS_NETWORK_STATE` - To check network connectivity status

## 🧪 Testing

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest
```

### Test Structure
- **Unit Tests**: Business logic and service testing
- **Integration Tests**: ADB service integration testing
- **UI Tests**: Compose UI testing with test rules

## 🔮 Future Enhancements

### Planned Features
- **File Manager**: Browse and transfer files between the device and the computer
- **Screen Recording**: Capture device screen recordings
- **Performance Monitoring**: CPU, memory, and battery usage tracking
- **Batch Operations**: Execute multiple commands across multiple devices
- **Custom Scripts**: Save and execute custom ADB command sequences
- **Export Functionality**: Export logs and device information

### Technical Improvements
- **Room Database**: Persistent storage for device history and logs
- **Background Services**: Long-running operations with notifications
- **Advanced Error Handling**: Retry mechanisms and offline support
- **Performance Optimization**: Memory management and battery efficiency
- **Accessibility**: Enhanced accessibility features and screen reader support

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit changes**: `git commit -m 'Add amazing feature'`
4. **Push to branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### Development Guidelines
- Follow Kotlin coding conventions
- Use Jetpack Compose best practices
- Maintain MVVM architecture patterns
- Add unit tests for new features
- Update documentation for API changes

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For support and questions:
- **Issues**: Open an issue on GitHub
- **Discussions**: Use GitHub Discussions for general questions
- **Documentation**: Check the docs/ folder for detailed guides

## 🙏 Acknowledgments

- **Android Team**: For Jetpack Compose and Material 3
- **Google**: For ADB and Android development tools
- **Community**: For open-source libraries and contributions
