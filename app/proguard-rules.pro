# Keep classes used by DLNA/UPnP library
-keep class org.fourthline.** { *; }

# Keep ExoPlayer reflection classes
-keep class com.google.android.exoplayer2.** { *; }

# Preserve Koin definitions that rely on reflection
-keep class org.koin.** { *; }
