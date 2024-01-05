package mir.oslav.jet.html.article.parse

/**
 * Java Native Interface (JNI) for the parser libary
 * @author Miroslav Hýbler <br>
 * created on 03.01.2023
 */
internal object ContentParserNative {



    init {
        //TODO auto initialization
        System.loadLibrary("article")
    }

    external fun setContent(content: String)


    external fun hasNextStep(): Boolean


    external fun hasContent(): Boolean


    external fun doNextStep()


    external fun getContent() :String
}