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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Repackage classes into the top-level.
-repackageclasses

# Rome reflectively loads classes referenced in com/rometools/rome/rome.properties.
-adaptresourcefilecontents com/rometools/rome/rome.properties
-keep,allowobfuscation class * implements com.rometools.rome.feed.synd.Converter
-keep,allowobfuscation class * implements com.rometools.rome.io.ModuleParser
-keep,allowobfuscation class * implements com.rometools.rome.io.WireFeedParser

# Disable warnings for missing classes from OkHttp.
-dontwarn org.conscrypt.ConscryptHostnameVerifier

# Disable warnings for missing classes from JDOM.
-dontwarn org.jaxen.DefaultNavigator
-dontwarn org.jaxen.NamespaceContext
-dontwarn org.jaxen.VariableContext
