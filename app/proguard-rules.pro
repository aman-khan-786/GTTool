# Keep GT Tool classes
-keep class com.arman.dev.gttool.** { *; }

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep Shizuku
-keep class rikka.shizuku.** { *; }
-keep class moe.shizuku.** { *; }

# Keep AIDL
-keep class * implements android.os.IInterface {
    *;
}

# Keep Compose
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>();
}

# Keep Coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}