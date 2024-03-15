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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class androidx.room.*
-keep class com.squareup.moshi.*
-keep class org.quicksc0p3r.simplecounter.db.Counter
-keep class org.quicksc0p3r.simplecounter.db.Label
-keep class org.quicksc0p3r.simplecounter.json.CountersAndLabels
-keepclassmembers class org.quicksc0p3r.simplecounter.db.Counter {*;}
-keepclassmembers class org.quicksc0p3r.simplecounter.db.Label {*;}
-keepclassmembers class org.quicksc0p3r.simplecounter.json.CountersAndLabels {*;}

-keepattributes Signature
-keepattributes *Annotation*
-keepnames class org.quicksc0p3r.simplecounter.db.Counter
-keepnames class org.quicksc0p3r.simplecounter.db.Label
-keepnames class org.quicksc0p3r.simplecounter.json.CountersAndLabels