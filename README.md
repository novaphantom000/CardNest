# CardNest

**CardNest** is a private, secure card information vault for Android. Store your credit and debit card details locally on your device with strong encryption—no cloud sync, no tracking, no ads.

## Features

- 🔒 **Local-only storage** — All data stays on your device
- 🔐 **Strong encryption** — AES-256-GCM with Android Keystore
- 👆 **Biometric authentication** — Fingerprint or device PIN/pattern
- 💳 **Virtual card designs** — Beautiful, network-appropriate card visualizations
- 🔍 **Search and organize** — Find cards by nickname, issuer, or last four digits
- 🔔 **Expiry reminders** — Optional notifications before cards expire
- 💾 **Encrypted backups** — Export/import with passphrase protection
- 🎨 **Material You** — Dynamic colors and adaptive theming
- ♿ **Accessible** — Screen reader support and reduced motion options

## Security

CardNest is designed with security-first principles:

- **Encryption at rest**: Card numbers are encrypted using AES-256-GCM with keys stored in Android Keystore (hardware-backed when available)
- **No CVV storage**: CVV/CVC codes are held in memory only during the session and cleared when the app locks
- **Biometric protection**: Vault access requires biometric authentication or device credential
- **No network access**: The app has no internet permission and never transmits your data
- **Backup excluded**: All app data is excluded from Android cloud backups and device transfers
- **No logging**: Sensitive data is never logged or included in crash reports

### Security Limitations

⚠️ **CardNest is NOT suitable for:**
- Devices that are rooted or have custom ROMs
- Environments where the operating system integrity is compromised
- Compliance with PCI DSS or other payment card processing standards

⚠️ **Known limitations:**
- Other apps with clipboard access can read copied card details
- Screenshot protection is limited on some devices
- Encrypted backups are only as strong as the user's passphrase
- No protection against physical device extraction or forensic analysis

## Privacy

CardNest collects **no data**:

- No analytics or tracking
- No crash reporting with identifiable information
- No network connectivity
- No accounts or registration
- No third-party services

For full privacy details, see the in-app Privacy Policy.

## Requirements

- Android 8.0 (API 26) or higher
- Biometric hardware (fingerprint, face unlock) or device PIN/pattern/password

## Installation

### From GitHub Releases

1. Download the latest APK from the [Releases](https://github.com/novaphantom000/CardNest/releases) page
2. Enable "Install from Unknown Sources" in your device settings
3. Install the APK

### Build from Source

```bash
git clone https://github.com/novaphantom000/CardNest.git
cd CardNest
./gradlew assembleRelease
```

The unsigned release APK will be in `app/build/outputs/apk/release/`.

## Development

### Prerequisites

- JDK 17 or higher
- Android SDK with API 35
- Android Studio (recommended) or command-line tools

### Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run lint
./gradlew lint
```

### Architecture

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Database**: Room (with application-level encryption for card numbers)
- **DI**: Hilt
- **Async**: Coroutines and Flow
- **Architecture**: MVVM with Repository pattern

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Submit a pull request

Security-related contributions should be reported privately via GitHub Security Advisories.

## License

This project is released under the [MIT License](LICENSE).

## Disclaimer

CardNest is provided "as is" without warranty of any kind. The authors are not responsible for any data loss, security breaches, or misuse of payment card information. Users are responsible for securing their devices and managing their data.

**This app does not process payments, connect to banks, or handle card transactions. It is a local storage tool only.**

---

Built with ❤️ using Jetpack Compose and Material 3
