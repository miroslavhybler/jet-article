**🛑 This library is no longer in develoopment as the scalability of the implementation was not so great, new library will be crated with usage of new [Html-Iterator](https://github.com/miroslavhybler/android-html-iterator) library. 🛑**




# Jet Html Article

Jet Html Article is experimental library for processing textual "article" based html content and
showing it natively using [Jetpack Compose](https://developer.android.com/jetpack/compose)
and [Material You](https://m3.material.io/). It's purpouse is to replace webview for
showing html content within application.

Main goals are:

* Better UX - Showing elements using Material3 design, support of dark mode, no annoying cookies bar
  or other interactive elements interupting user
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
    isTextFormattingEnabled = true,
)

//Add custom exclude options to filter out unwanted elements
ArticleParser.addExcludeOption(
    tag = "menu"
)

//Load html content, you need to load html code and put it into string variable
val content: String = TODO("Load html content")
//Process the content, do parsing on background to not interupt UI thread
val data = JetHtmlArticleParser.parse(
    content = content,
    url = TODO("Original url of the article")
)
//Show the data using composable function
val state = rememberJetHtmlArticleState()

state.show(data = data)

JetHtmlArticle(
  state = state,
    modifier = Modifier,
    contentPadding = paddingValues
)
```

#### Customize UI

To customize UI use `JetHtmlArticleContent` where you can pass custom composables for items.
Composable
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
    },
  image = { image ->
        //Passing custom image composable
        CustomHtmlImage(
            modifier = Modifier.animateContentSize(),
            image = image,
        )
    }
)
```

&nbsp;

&nbsp;

### Other usages

#### Links Handling

To have custom link handling logic you have to create custom implementation of
`LinkClickHandler.LinkCallback` and provide it to the `HtmlArticleData`.
Creating custom `LinkCallback`:

```kotlin
    val customLinkCallback = remember {
    object : LinkClickHandler.LinkCallback() {
        override fun onOtherDomainLink(link: Link.OtherDomainLink) {
            //Links leeads to website that is on another domain
        }

        override fun onSameDomainLink(link: Link.SameDomainLink) {
            //Links leeads to website on same domain
        }

        override fun onUriLink(link: Link.UriLink, context: Context) {
            //Uri link (can be email, phone, ...)
        }

        override fun onSectionLink(
            link: Link.SectionLink,
            lazyListState: LazyListState,
            data: HtmlArticleData,
            scrollOffset: Int,
        ) {
            //Leads to some part of currently loaded website, scroll to the section
          }
        } 
    }
```

Providing it to `HtmlArticleData`:

```kotlin
    data.linkHandler.callback = customLinkCallback

```

#### Filtering content

Use `ArticleParser#addExcludeOption` to exclude certain elements from output. Elements can be
excluded by tag, class, id or keyword. When using keyword, filter will look for those word in tag,
clazz or id. Keep in mind that **using keyword** is worst in matter of performance and speed.

```kotlin
   fun addExcludeOption(
    tag: String = "",
    clazz: String = "",
    id: String = "",
    keyword: String = "",
)
```

#### Support various types of images

Jet-Article is using [Coil library](https://coil-kt.github.io/coil/) for showing images, to add
support for various image formats follow Coil documentation. `HtmlImage` composable is using
`context.imageLoader` extension, so your `Application` class needs to implement `ImageLoaderFactory`
interace and override `newImageLoader()` to provide loader that supports desired format like svg or
gif.
E.g. [adding svg format support](https://coil-kt.github.io/coil/svgs/):

```kotlin
class MyApplication : Application(), ImageLoaderFactory {

    //Adding svg support to application class, HtmlImage composable is usign SubcomposeAsyncImage
    //with imageLoader provided by context.imageLoader extension.
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}
```

&nbsp;

&nbsp;

## Why project like this?

Idea for this project comes from frustrating experienses using `WebView` for showing simple HTML
content like articles, posts and sililar text based content. Usage of `WebView` with custom third
party websites often leads to these issues:

* UI junks - Websites are not well optimised or using too much javascript, casing it to load slow
  and with junk frames
* Browser features - Some Websites can **depend on** on browser features which are naturally not
  avalilabe in `WebView`. As result this making user experience worse.
* No support for dark mode - Dark mode is really popular but lots of websites still doesn't support
  it. For user it's really annoying when all parts of the app cannot be shown in same mode.
* Unwanted UI elements - Based on web architecture sometimes it's imposible to remove web
  interactive elements like website menu, cookies bar, ... giving user the opportunity to get
  stucked inside webview navigation.

This library is trying to solve those issues by parsing content throught c++ code for maximal
possible performace and then showing content
using [Jetpack Compose](https://developer.android.com/jetpack/compose)
and [Material You](https://m3.material.io/) desing to boost user experience within final app. But
keep in mind this library is still in development and it's hightly experimental.

If you are interested take a look
at [Dev Blog for Android](https://github.com/miroslavhybler/Dev-Blog-for-Android-App) example which
is using implementation of JetArticle to
show [Android Developer Blog](https://android-developers.googleblog.com/).
