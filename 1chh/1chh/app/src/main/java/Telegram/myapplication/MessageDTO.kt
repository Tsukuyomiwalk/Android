package Telegram.myapplication

import Telegram.db.MessagesEntity
import android.graphics.Bitmap
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class MessageDTO(
    @JsonProperty("id")
    val num: Int? = null,
    @JsonProperty("from")
    val user: String,
    @JsonProperty("data")
    val message: Data,
    @JsonProperty("time")
    val time: String? = null
) {
    fun toMessageEntity(): MessagesEntity {
        return MessagesEntity(
            num!!,
            num.toLong(),
            user,
            message.text?.text,
            message.image?.link,
            time!!
        )
    }
}

data class Pic(val link: String, var image: Bitmap? = null)
data class Text(val text: String)
data class Data(
    val image: Pic? = null,
    val text: Text? = null
)