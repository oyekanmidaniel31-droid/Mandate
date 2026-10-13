# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses, Signature
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepnames class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Jetpack Compose & AudioTrack retention
-keep class com.example.mandate.audio.FujiAudioEngine { *; }
-keep class com.example.mandate.model.** { *; }
-keep class com.example.mandate.viewmodel.** { *; }

