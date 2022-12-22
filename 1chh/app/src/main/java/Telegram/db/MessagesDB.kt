package Telegram.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MessagesEntity::class], version = 1)

abstract class MessagesDB : RoomDatabase() {
    abstract fun messagesDAO(): MessagesDAO

    companion object {

        @Volatile
        private var INSTANCE: MessagesDB? = null

        fun getDatabase(context: Context): MessagesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): MessagesDB {
            return Room.databaseBuilder(
                    context.applicationContext,
                    MessagesDB::class.java,
                    "messages"
            ).build()
        }
    }

}