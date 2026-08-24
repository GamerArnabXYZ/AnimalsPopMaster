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

# Google Play Services Ads ke liye rules
-keep class com.google.android.gms.ads.** { *; }

# Natty Engine ke liye rules (Reflection issues se bachne ke liye)
# NOTE: actual package "com.nativegame.nattyengine" hai (imports check kiye),
# "com.github.nativegamestudio.nattyengine" wala pehle wala rule kabhi match hi
# nahi karta tha -> minifyEnabled=true ke saath engine crash ka risk tha.
-keep class com.nativegame.nattyengine.** { *; }
-keep interface com.nativegame.nattyengine.** { *; }

# AndroidX / AppCompat ke core classes safe rakhne ke liye (naye AGP/R8 ke saath extra safety)
-keep class androidx.appcompat.** { *; }
-dontwarn com.google.android.gms.**
