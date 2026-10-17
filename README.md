# WatchParty - Synchronized Movie Watch Party App

A native Android application that enables multiple users to watch the same locally-stored movie file together in real-time synchronization.

## Features

- **Room Management**: Create or join rooms with 6-character codes
- **File Verification**: SHA-256 hash verification ensures all participants have identical files
- **Precise Synchronization**: Sub-100ms drift with playback speed micro-corrections
- **Host Controls**: Only the room creator can control playback (play, pause, seek)
- **Server-Authoritative**: All commands validated server-side for security
- **Reconnection Handling**: Graceful recovery from network interruptions

## Architecture

### Android App
- **Native Android** with Kotlin
- **Jetpack Compose** for modern UI
- **ExoPlayer (Media3)** for precise video playback control
- **OkHttp** for WebSocket real-time communication
- **Coroutines** for asynchronous operations

### Backend
- **Node.js** with Express
- **WebSocket (ws)** for real-time synchronization
- Room-based architecture with host/participant roles

## Project Structure

```
WatchParty/
├── app/                          # Android application
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/watchparty/
│   │   │   ├── MainActivity.kt           # Entry point
│   │   │   ├── ui/
│   │   │   │   ├── WatchPartyApp.kt      # Main composable
│   │   │   │   ├── RoomScreen.kt         # Video player screen
│   │   │   │   └── theme/                # Theme files
│   │   │   ├── viewmodel/
│   │   │   │   └── WatchPartyViewModel.kt # State management
│   │   │   ├── model/
│   │   │   │   └── WatchPartyUiState.kt   # UI state models
│   │   │   ├── network/
│   │   │   │   ├── WebSocketClient.kt     # WebSocket connection
│   │   │   │   └── Protocol.kt            # Message protocol
│   │   │   ├── sync/
│   │   │   │   └── SyncEngine.kt          # Synchronization logic
│   │   │   └── util/
│   │   │       └── FileHasher.kt          # SHA-256 file verification
│   │   └── res/                  # Resources (strings, themes)
│   ├── build.gradle.kts          # App dependencies
│   └── proguard-rules.pro        # ProGuard configuration
├── backend/                      # Node.js WebSocket server
│   ├── server.js                 # Server implementation
│   └── package.json              # Node dependencies
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
└── gradle.properties             # Gradle properties
```

## Setup Instructions

### Prerequisites

1. **Android Studio** (Arctic Fox or later)
   - Download from: https://developer.android.com/studio
   - Includes Android SDK and build tools

2. **Node.js** (v16 or later)
   - Download from: https://nodejs.org

### Android App Setup

1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the `WatchParty` directory
4. Wait for Gradle sync to complete (this will download all dependencies)

5. **Configure server URL** (optional):
   - Open `WatchPartyViewModel.kt`
   - Find `private val serverUrl = "ws://10.0.2.2:3000"`
   - For physical devices, replace `10.0.2.2` with your computer's local IP address
   - For emulator, keep `10.0.2.2` (this is the emulator's localhost)

6. Connect an Android device or start an emulator
7. Click the "Run" button (green triangle) in Android Studio

### Backend Setup

1. Open a terminal
2. Navigate to the backend directory:
   ```bash
   cd WatchParty/backend
   ```

3. Install dependencies:
   ```bash
   npm install
   ```

4. Start the server:
   ```bash
   npm start
   ```

The server will start on port 3000 by default.

## How to Use

### Creating a Room

1. Launch the app
2. Tap "Create Room"
3. Note the 6-character room code displayed
4. Tap "Select Movie File" and choose a video file
5. Wait for file verification to complete
6. Share the room code with other participants
7. Once all participants are ready, tap "Start Playback"

### Joining a Room

1. Launch the app
2. Enter the 6-character room code
3. Tap "Join Room"
4. Tap "Select Movie File" and choose the **same** video file as the host
5. Wait for file verification
6. Wait for the host to start playback

## Technical Details

### Synchronization Algorithm

The app uses a multi-layered approach for sub-100ms synchronization:

1. **Clock Synchronization**: Estimates server time offset using ping-pong messages
2. **Scheduled Playback**: Server schedules play commands with future timestamps
3. **Drift Monitoring**: Checks actual vs. expected position every 100ms
4. **Playback Speed Correction**:
   - < 40ms drift: No correction
   - 40-100ms drift: Gentle correction (0.98x or 1.02x speed)
   - 100-250ms drift: Stronger correction (0.96x or 1.04x speed)
   - > 250ms drift: Full resync (seek to correct position)

### File Verification

- Files are hashed using SHA-256 in 8KB chunks
- Progress is reported during hashing
- The server ensures all participants have matching hashes
- Playback is blocked until verification succeeds

### Security

- Server validates that only the host can send playback commands
- Clients cannot force unauthorized actions
- File content never leaves the device (only hash is transmitted)
- WebSocket connections use standard security practices

## Known Limitations

- Requires manual server URL configuration for physical devices
- No TLS/SSL in current implementation (use for local networks only)
- Room codes are not persistent (server restart clears all rooms)
- No authentication system

## Future Enhancements

- [ ] Add TLS support for production use
- [ ] Implement user authentication
- [ ] Add chat functionality
- [ ] Persistent room storage
- [ ] Support for subtitles synchronization
- [ ] Network quality indicators
- [ ] Automatic server discovery

## Troubleshooting

### "Cannot connect to server"
- Ensure the backend server is running
- Check the server URL in `WatchPartyViewModel.kt`
- For physical devices, use your computer's local IP (not `10.0.2.2`)
- Ensure both device and server are on the same network

### "File hash mismatch"
- All participants must have the **exact same file**
- Different encodings or versions will fail verification
- Try re-downloading the file on all devices

### "Playback drift is too high"
- Check network quality
- Reduce background network activity
- Try using a wired connection for the server

## License

MIT License - See LICENSE file for details
