# AndroidCasting

A modular Android application template for casting photos, videos, audio and downloaded files to smart TVs. The project is designed around Clean Architecture and MVVM, supports codec analysis before casting, and exposes a compatibility mode powered by a local HTTP streaming server.

## Modules

- **app** – Android application entry point hosting the Compose navigation graph and Hilt setup.
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

1. Open the project in Android Studio (Giraffe or newer).
2. Sync Gradle to download dependencies (ExoPlayer, Cling, NanoHTTPD, Hilt, Compose, etc.).
3. Provide the required runtime permissions (storage, Wi-Fi state, multicast) when prompted on device.
4. Implement the remaining DLNA transport commands inside `DlnaCastingRepository` to complete casting flows.

### Verifying the Latest Template Commit

If you cloned this repository but cannot see the most recent scaffolding, confirm that you are on the `work` branch and that the commit `feat: scaffold modular android casting app` (hash `d1f6295`) is present. From the project root you can run:

```bash
git checkout work
git pull --ff-only
git log -1
```

The final command should show the feature commit mentioned above. If it does not, the branch likely has not been pushed to your remote yet. Push it from your local machine with:

```bash
git push origin work
```

After pushing, the updates will be visible on your Git hosting provider and accessible to collaborators.

## Next Steps

- Implement MediaProjection-based screen mirroring and Miracast support.
- Extend the codec matrix with device-specific capability querying.
- Add remote control UI (D-Pad, volume, power/input toggles).
- Build diagnostic screens for Wi-Fi and DLNA troubleshooting.

This template provides the scaffolding, documentation and comments necessary to evolve into a production-ready casting client.
