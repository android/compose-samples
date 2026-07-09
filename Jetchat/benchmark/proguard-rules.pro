# Proguard rules for the benchmark test module.
# Since this is a test APK, we don't want R8 to fail on missing classes that are either
# provided by the tested application at runtime or are unused transitives.

-dontwarn androidx.arch.core.**
-dontwarn androidx.profileinstaller.**
-dontwarn androidx.startup.**
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.lang.model.**
-ignorewarnings


