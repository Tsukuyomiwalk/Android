package Telegram.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FailedMessagesEntity::class], version = 1)

abstract class FailedMessagesDB : RoomDatabase() {
    abstract fun failedMessagesDAO(): FailedMessagesDAO

    companion object {

        @Volatile
        private var INSTANCE: FailedMessagesDB? = null

        fun getDatabase(context: Context): FailedMessagesDB {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }

        private fun buildDatabase(context: Context): FailedMessagesDB {
            return Room.databaseBuilder(
                    context.applicationContext,
                    FailedMessagesDB::class.java,
                    "failed_messages"
            ).build()
        }
    }

}