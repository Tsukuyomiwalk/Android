package Telegram.db

import Telegram.myapplication.Data
import Telegram.myapplication.MessageDTO
import Telegram.myapplication.Pic
import Telegram.myapplication.Text
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "failed_messages")
class FailedMessagesEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val from: String,
    val text: String?
)