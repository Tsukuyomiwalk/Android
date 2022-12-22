package Telegram.db

import Telegram.myapplication.Data
import Telegram.myapplication.MessageDTO
import Telegram.myapplication.Pic
import Telegram.myapplication.Text
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
class MessagesEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "image_id") val imageId: Long? = null,
    val from: String,
    val text: String?,
    val link: String?,
    val time: String
) {
    fun toMessageDTO(): MessageDTO {
        return if (text != null) {
            MessageDTO(id, from, Data(text = Text(text)), time)
        } else {
            MessageDTO(id, from, Data(image = Pic(link!!)), time)
        }
    }
}