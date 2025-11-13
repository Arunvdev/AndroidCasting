# AndroidCasting

A modular Android application template for casting photos, videos, audio and downloaded files to smart TVs. The project is designed around Clean Architecture and MVVM, supports codec analysis before casting, and exposes a compatibility mode powered by a local HTTP streaming server.

## Modules

- **app** – Android application entry point hosting the Compose navigation graph and bootstrapping Koin.
- **core** – Shared utilities such as the local HTTP server, security helpers and result wrappers.
- **data** – Repository implementations for media scanning, DLNA/UPnP control and codec detection.
- **domain** – Business logic with use cases, models and repository interfaces.
- **player** – ExoPlayer configuration for local preview playback.
- **ui** – Jetpack Compose UI layer, navigation and shared ViewModel.

## Key Features

- Browse and preview local media with ExoPlayer before casting.
- Analyse codecs and containers to warn users about potential incompatibilities.
- Discover DLNA/UPnP renderers and stream content either directly or via compatibility mode using a local HTTP server.
- Secure foundations with encrypted storage, ProGuard/R8 enabled and modular separation for future anti-tampering logic.
- Architecture ready for future expansions such as screen mirroring, Miracast and remote control features.

## Getting Started

1. Clone the project and run `./gradlew assembleDebug` (Gradle Wrapper 8.7) to verify the build.
2. Open the project in Android Studio (Giraffe or newer) and let it sync dependencies (Compose, ExoPlayer, Cling, NanoHTTPD, Coil, Koin, etc.).
3. Deploy to a physical device on the same Wi-Fi as your target TV. The app requests Android 13+ media permissions (or `READ_EXTERNAL_STORAGE` on earlier versions) before scanning files.
4. Browse the media grid, preview with ExoPlayer, review codec compatibility guidance, then cast via the DLNA transport. If the renderer cannot handle the codecs, enable compatibility mode to stream from the embedded HTTP server.

### Production Notes

- `DlnaCastingRepository` now issues `SetAVTransportURI`, `Play`, and `Stop` actions against renderers and keeps a multicast lock for stable discovery.
- The embedded HTTP server streams content directly from `ContentResolver`, supports byte range requests, and generates DLNA metadata so TVs can buffer efficiently.
- Scoped-storage compliant `MediaStore` queries handle images, videos, audio, and general files while gracefully retrying once permissions are granted.
- Security-sensitive configuration uses `EncryptedSharedPreferences`, and release builds ship with R8 obfuscation enabled.

## Next Steps

- Implement MediaProjection-based screen mirroring and Miracast support.
- Extend the codec matrix with device-specific capability querying.
- Add remote control UI (D-Pad, volume, power/input toggles).
- Build diagnostic screens for Wi-Fi and DLNA troubleshooting.

This template provides the scaffolding, documentation and comments necessary to evolve into a production-ready casting client.
