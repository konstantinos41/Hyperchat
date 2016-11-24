# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Konstantinos\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keepattributes EnclosingMethod
-keepattributes InnerClasses

 #keep json classes
 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonMarker {
     !static !transient <fields>;
 }

 -keepclassmembernames class * extends com.applozic.mobicommons.json.JsonParcelableMarker {
     !static !transient <fields>;
 }
 #GSON Config
-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class org.eclipse.paho.client.mqttv3.logging.JSR47Logger { *; }
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
