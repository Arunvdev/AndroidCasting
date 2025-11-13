# Keep Hilt generated components
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.ComponentEntryPoint

# Keep classes used by DLNA/UPnP library
-keep class org.fourthline.** { *; }

# Keep ExoPlayer reflection classes
-keep class com.google.android.exoplayer2.** { *; }
