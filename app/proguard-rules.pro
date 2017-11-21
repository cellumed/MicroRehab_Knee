# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ikoob/Library/Android/sdk/tools/proguard/proguard-android.txt
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


-dontwarn org.apache.**
-dontwarn butterknife.**
-dontwarn com.cellumed.healthcare.**
-dontwarn android.support.v4.**

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Add any project specific keep options here:
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class javax.**
-keep class org.apache.** {*;}
-keep interface org.apache.** {*;}
-keep class org.*
-keep class android.** { *; }
-keep class org.sqlite.** { *; }
-keep class org.sqlite.database.** { *; }
-keep class com.db.models.**
-keepclassmembers class com.db.models.** { *; }
-keep public class android.database.sqlite.**
-keep class com.github.mikephil.charting.** {*;}

# Don't note duplicate definition (Legacy Apche Http Client)
-dontnote android.net.http.*
-dontnote org.apache.http.**

# Add when compile with JDK 1.7
-keepattributes EnclosingMethod

# Firebase Authentication
-keepattributes *Annotation*

# Firebase Realtime database
-keepattributes Signature

-dontwarn okhttp3.**
-dontwarn okio.**

-dontnote okhttp3.**

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions



-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-dontwarn rx.internal.**
-dontnote rx.internal.**