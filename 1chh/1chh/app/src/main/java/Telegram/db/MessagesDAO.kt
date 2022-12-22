package Telegram.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MessagesDAO {

    @Insert
    fun insert(message: MessagesEntity)

    @Insert
    fun insert(message: List<MessagesEntity>)

    @Query("select * from messages")
    fun getAll(): List<MessagesEntity>
}