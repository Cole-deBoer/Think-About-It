# Think-About-It

Think-About-It is an innovative Android game where players create and share their artistic interpretations of various conspiracy theories. The game combines creativity, social interaction, and friendly competition in a unique drawing-based experience.

## 🎮 Game Overview

Think-About-It is a multiplayer drawing game where players:
1. Receive a conspiracy theory prompt
2. Create their artistic interpretation within a time limit
3. Vote on other players' drawings
4. Earn points based on votes received
5. Compete for the top spot on the leaderboard

## 🎨 Features

- **Interactive Drawing Canvas**: Create detailed drawings with customizable brush sizes and colors
- **Real-time Voting System**: Vote for your favorite interpretations
- **Leaderboard**: See which drawings received the most votes
- **Multiplayer Support**: Play with friends in real-time
- **Firebase Integration**: Secure user authentication and data storage
- **Modern UI**: Clean and intuitive user interface with dark theme

## 📱 Screenshots

![naming](https://github.com/user-attachments/assets/2fdb893b-01e0-45fb-aae8-720ffc143a46)

![Drawing](https://github.com/user-attachments/assets/fe6d0443-e366-449f-b845-0a94d0bce7b8)

![Voting](https://github.com/user-attachments/assets/3c16e29d-24ad-486d-99cf-5dbea9015c47)

![Leaderboard](https://github.com/user-attachments/assets/509ae850-a9f4-43c3-9b8d-c18df0d2019d)

## 🛠️ Technical Stack

- **Language**: Kotlin
- **Minimum SDK**: 27 (Android 8.1)
- **Target SDK**: 35
- **Dependencies**:
  - Firebase (Authentication, Realtime Database, Storage)
  - AndroidX Core
  - Material Design Components
  - ConstraintLayout
  - Kotlin Coroutines for async operations
  - ViewModel and LiveData for UI state management

## 🚀 Getting Started

### Prerequisites

- Android Studio Arctic Fox or newer
- Android SDK 27 or higher
- Google Play Services
- Firebase account

### Installation

#### Download latest release

1. Go to the [Releases](https://github.com/Cole-deBoer/Think-About-It/releases) page
2. Download the latest APK file
3. Enable "Install from Unknown Sources" in your Android settings
4. Install the APK

#### Manually Through Android Studio:

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/Think-About-It.git
   ```

2. Open the project in Android Studio

3. Set up Firebase:
   - Create a new Firebase project
   - Add your Android app to the Firebase project
   - Download `google-services.json` and place it in the `app` directory
   - Enable Authentication, Realtime Database, and Storage in Firebase Console

4. Build and run the project

## 🎯 Game Rules

1. **Drawing Phase**:
   - Players receive a conspiracy theory prompt
   - 2-minute time limit to create your drawing
   - Use various brush sizes and colors
   - Save your drawing when done

2. **Voting Phase**:
   - View all players' drawings anonymously
   - Vote for your favorite interpretations
   - Each player gets 3 votes
   - Cannot vote for your own drawing

3. **Scoring**:
   - Points awarded based on votes received
   - Bonus points for creative interpretations
   - Weekly and all-time leaderboards

## 👥 Collaborators

Cole - https://github.com/Cole-deBoer

Reid - https://github.com/PG27Reid

Simone - https://github.com/PG27Simone

Jason - https://github.com/PG27Jason

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **Bug Reports**:
   - Use the GitHub Issues section
   - Include steps to reproduce
   - Add screenshots if applicable
   - Specify your device and Android version

2. **Feature Requests**:
   - Create a new issue with the "enhancement" label
   - Describe the feature in detail
   - Explain its benefits to the game

3. **Code Contributions**:
   - Fork the repository
   - Create a feature branch
   - Follow our coding style
   - Write tests for new features
   - Submit a pull request

4. **Documentation**:
   - Improve existing documentation
   - Add comments to complex code
   - Update README with new features

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔄 Version History

- **v0.1.0-alpha.1** (Current)
  - Initial pre-release
  - Basic drawing functionality
  - Firebase integration
  - Multiplayer support
  - Voting system

## 🔜 Roadmap

- [ ] Additional conspiracy theory prompts
- [ ] Enhanced drawing tools
- [ ] Social media sharing
- [ ] Custom game rooms
