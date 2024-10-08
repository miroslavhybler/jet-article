```ascii
ooooo     ooo                   .o8                          oooooooooo.                                   oooo                                                                   .   
`888'     `8'                  "888                          `888'   `Y8b                                  `888                                                                 .o8   
 888       8  ooo. .oo.    .oooo888   .ooooo.  oooo d8b       888      888  .ooooo.  oooo    ooo  .ooooo.   888   .ooooo.  oo.ooooo.  ooo. .oo.  .oo.    .ooooo.  ooo. .oo.   .o888oo 
 888       8  `888P"Y88b  d88' `888  d88' `88b `888""8P       888      888 d88' `88b  `88.  .8'  d88' `88b  888  d88' `88b  888' `88b `888P"Y88bP"Y88b  d88' `88b `888P"Y88b    888   
 888       8   888   888  888   888  888ooo888  888           888      888 888ooo888   `88..8'   888ooo888  888  888   888  888   888  888   888   888  888ooo888  888   888    888   
 `88.    .8'   888   888  888   888  888    .o  888           888     d88' 888    .o    `888'    888    .o  888  888   888  888   888  888   888   888  888    .o  888   888    888 . 
   `YbodP'    o888o o888o `Y8bod88P" `Y8bod8P' d888b         o888bood8P'   `Y8bod8P'     `8'     `Y8bod8P' o888o `Y8bod8P'  888bod8P' o888o o888o o888o `Y8bod8P' o888o o888o   "888" 
                                                                                                                            888                                                       
                                                                                                                           o888o                                                      
                                                                                                                                                                                      
```

This library is currently 🚧 **UNDER DEVELOPMENT** 🚧, there are no usable releases yet.

# Jet Html Article

Jet Html Article is experimental library for processing textual "article" based html content and 
showing it natively using Jetpack Compose and Material You. It's purpouse is to replace webview for
showing html content within application.


Main goals are:
* Better UX - Showing elements using Material3 design, support of dark mode, no annoying cookies bar or other interactive elements interupting user
* Speed - since all scripts, cookies are exluded it could work much faster than webview
* User Privacy - since all scripts are disabled, there are no statictics or t


[//]: # (This is still under development, no relases yet)
[//]: # (### Import)

[//]: # (Add this maven to your project gradle or your settings.gradle.kts)

[//]: # ()
[//]: # (```kotlin)

[//]: # (maven&#40;url = "https://jitpack.io"&#41;)

[//]: # (```)

[//]: # ()
[//]: # (Then add library dependency into your app build.gradle.kts)

[//]: # ()
[//]: # (```kotlin)

[//]: # (implementation&#40;"com.github.miroslavhybler:jet-html-article:1.0.0-alpha01"&#41;)

[//]: # (```)


### Usage
Add Internet permission to your application's manifest. Internet is required for loading images.
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

```kotlin
//Setup library 
ArticleParser.initialize(
    areImagesEnabled = false,
    isLoggingEnabled = true,
    isSimpleTextFormatAllowed = true,
)

//Add custom exclude options to filter out unwanted elements
ArticleParser.addExcludeOption(
    tag = "menu"
)

//Load html content, you need to load html code and put it into string variable
val content:String = TODO("Load html content")
//Process the content
val data = JetHtmlArticleParser.parse(
    content = content,
    url = TODO("Original url of the article")
)
//Show the data using composable function
JetHtmlArticle(
    data = data,
    modifier = Modifier,
    contentPadding = paddingValues
)
```

#### Customize UI
To customize UI use `JetHtmlArticleContent` where you can pass custom composables for items. Composable
lambdas are providing `HtmlElement` attribute
e.g.
```kotlin
JetHtmlArticleContent(
    data = post.postData,
    text = { text ->
        CustomHtmlText(
            modifier = Modifier.animateContentSize(),
            image = image,
        )
    }
    image = { image ->
        CustomHtmlImage(
            modifier = Modifier.animateContentSize(),
            image = image,
        )
    }
)
```

### Quick showcases for usage (TODO)

#### Links Handling

#### Filtering content

#### Support various types of images