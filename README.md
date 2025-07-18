# NetScope v1.0 📡

**A comprehensive Android network analysis and scanning application built with Jetpack Compose and Material 3.**

## 🚀 Overview

NetScope is a powerful network analysis tool for Android that provides comprehensive network discovery, port scanning, Wi-Fi analysis, and diagnostic utilities. Built with modern Android development practices using Jetpack Compose, MVVM architecture, and Material 3 design system.

## ✨ Features

### 🔍 Network Discovery
- **Device Scanning**: Discover all devices on your local network
- **Device Information**: View IP addresses, MAC addresses, hostnames, and device types
- **Real-time Status**: Live scanning with progress indicators and device status
- **Device Classification**: Automatic identification of routers, computers, mobile devices, printers, and IoT devices

### 🔧 Port Analysis Tools
- **TCP/UDP Port Scanner**: Scan custom port ranges on target devices
- **Service Identification**: Automatic detection of common services (SSH, HTTP, HTTPS, etc.)
- **Real-time Progress**: Live scanning progress with port-by-port updates
- **Detailed Results**: Port status, response times, and service banners

### 📶 Wi-Fi Analysis Dashboard
- **Current Connection**: Detailed information about your connected network
- **Signal Monitoring**: Real-time signal strength and quality metrics
- **Network Discovery**: Scan and analyze all available Wi-Fi networks
- **Security Analysis**: Display security protocols and encryption methods
- **Channel Analysis**: Frequency and channel information for interference detection

### 📊 Reports & Export
- **Scan History**: Complete history of all network scans with timestamps
- **Export Options**: Export results in JSON and PDF formats
- **Share Functionality**: Share scan results via email, messaging, or cloud storage
- **Filtering**: Filter scan history by type, date, and results

## 🏗️ Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Navigation Compose
- **State Management**: StateFlow & Compose State
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)

### Project Structure
```
app/src/main/java/com/app/netscope/
├── data/
│   └── model/              # Data models and entities
├── presentation/
│   ├── navigation/         # Navigation setup
│   ├── scanner/           # Network discovery screens
│   ├── tools/             # Port scanning tools
│   ├── wifi/              # Wi-Fi analysis screens
│   └── reports/           # Reports and export screens
└── ui/
    └── theme/             # Material 3 theming
```

## 🛠️ Setup & Installation

### Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26+
- Kotlin 1.9+

### Build Instructions
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd NetScope
   ```

2. **Build the project**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Install on device**
   ```bash
   ./gradlew installDebug
   ```

4. **Run from Android Studio**
   - Open the project in Android Studio
   - Click the "Run" button or press Shift+F10

## 📱 Usage Guide

### Network Scanner
1. Open the **Scanner** tab from the bottom navigation
2. Tap the **Play** button to start network discovery
3. View discovered devices with their details
4. Tap on any device card for more information

### Port Scanner
1. Navigate to the **Tools** tab
2. Enter the target IP address
3. Set the port range (start and end ports)
4. Tap **Scan** to begin port analysis
5. View results showing open ports and services

### Wi-Fi Analysis
1. Go to the **Wi-Fi Info** tab
2. View your current connection details
3. Tap **Refresh** to scan for available networks
4. Analyze signal strengths and security protocols

### Reports & Export
1. Access the **Reports** tab
2. View your scan history
3. Use **Export** buttons to save results as JSON or PDF
4. Tap **Share** on any scan to share results

## 🔒 Permissions

NetScope requires the following permissions:

- `INTERNET` - For network operations
- `ACCESS_NETWORK_STATE` - To check network connectivity
- `ACCESS_WIFI_STATE` - For Wi-Fi information
- `CHANGE_WIFI_STATE` - For Wi-Fi scanning
- `ACCESS_FINE_LOCATION` - Required for Wi-Fi scanning on Android 6+
- `WAKE_LOCK` - For background scanning operations

## 🎨 UI Components

### Key Screens
- **MainActivity**: Main navigation hub with bottom navigation
- **ScannerScreen**: Network discovery interface with device list
- **ToolsScreen**: Port scanning tools with input controls
- **WifiScreen**: Wi-Fi analysis dashboard
- **ReportsScreen**: Scan history and export functionality

### Design Features
- **Material 3 Design**: Modern, accessible interface
- **Dark/Light Theme**: Automatic theme switching
- **Responsive Layout**: Optimized for different screen sizes
- **Progress Indicators**: Real-time feedback for long operations
- **Status Badges**: Color-coded information display

## 🔮 Future Enhancements

### Planned Features
- **Real Network Implementation**: Replace mock data with actual network scanning
- **Background Services**: Long-running scans with notifications
- **Hilt Dependency Injection**: Proper DI setup for scalability
- **Database Integration**: Room database for persistent storage
- **Advanced Diagnostics**: Ping, traceroute, DNS lookup, WHOIS
- **Network Security Analysis**: Vulnerability detection and reporting
- **Custom Scan Profiles**: Save and reuse scanning configurations

### Technical Improvements
- **Unit Testing**: Comprehensive test coverage
- **Integration Testing**: End-to-end testing scenarios
- **Performance Optimization**: Memory and battery usage optimization
- **Error Handling**: Robust error states and recovery
- **Accessibility**: Enhanced accessibility features

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📞 Support

For support, please open an issue on the GitHub repository.

---

**NetScope v1.0** - Built with ❤️ using Jetpack Compose and Material 3
