package Telegram.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FailedMessagesDAO {

    @Insert
    fun insert(message: FailedMessagesEntity)

    @Delete
    fun delete(message: FailedMessagesEntity)

    @Query("select * from failed_messages")
    fun getAll(): List<FailedMessagesEntity>
}