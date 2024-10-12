
# Keeping data classes as they are highly problematic when obscufated
-keep class com.jet.article.data.*
-keep class com.jet.article.ArticleParser { *; }
-keep class com.jet.article.ArticleAnalyzer { *; }

# Keeping compose functions
-keep class com.jet.article.ui.* {
    public static <methods>;
}

# Keeping all native JNIs and public interface of the library
-keep class com.jet.article.NativeLibraryInitializer { *; }
-keep class com.jet.article.AnalyzerNative { *; }
-keep class com.jet.article.ParserNative { *; }
-keep class com.jet.article.ContentFilterNative { *; }
-keep class com.jet.article.UtilsNative { *; }
-keep class com.jet.article.ui.LinkClickHandler { *; }

# Keeping packaged names for whole library
-keeppackagenames com.jet.article.**
-keepnames class com.jet.article.**



# Keeping compose
-keep @androidx.compose.runtime.Composable class * { *; }
-keep class androidx.compose.** { *; }
-keep class kotlin.Metadata { *; }
-keepclassmembers class ** {
    @androidx.compose.runtime.Composable *;
}
-keepclassmembers class ** {
    public static <methods>;
}

-keep class **$$ExternalSyntheticLambda* { *; }
-keepclassmembers class **$$ExternalSyntheticLambda* { *; }