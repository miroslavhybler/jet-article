package jet.html.article.example.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/**
 * @author Miroslav Hýbler <br>
 * created on 05.02.2024
 * @since 1.0.0
 */
@Parcelize
data class ExcludeRule constructor(
    val tag: String,
    val clazz: String = ""
) : Parcelable {


    companion object {
        var globalRules: List<ExcludeRule> = emptyList()
    }

}