# Jet Article

Jet Html Article is experimental library for processing textual "article" based html content and 
showing it natively using Jetpack Compose and Material You.


### Import
Add this maven to your project gradle or your settings.gradle.kts

```kotlin
maven(url = "https://jitpack.io")
```

Then add library dependency into your app build.gradle.kts

```kotlin
implementation("com.github.miroslavhybler:jet-html-article:1.0.0-alpha01")
```


### Usage
Add Internet permission to your application's manifest. Internet is required for loading images.
```xml
    <uses-permission android:name="android.permission.INTERNET" />
```

```kotlin
//Load html content
val content = TODO("Load html content")
//Process the content
val data = JetHtmlArticleParser.parse(content=content)
//Show the data using composable function
JetHtmlArticle(
    data = data,
    modifier = Modifier,
    contentPadding = paddingValues
)
```
