# Add project specific ProGuard rules here.
# Keep data models and core game logic classes intact when minification is enabled.
-keep class com.rainbowunicorn.core.** { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep *;
}
