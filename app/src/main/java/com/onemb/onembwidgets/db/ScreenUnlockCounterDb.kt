package com.onemb.onembwidgets.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScreenUnlockCounterInterface::class], version = 1, exportSchema = false)
abstract class ScreenUnlockCounterDb : RoomDatabase() {
    abstract fun screenUnlockCounterDao(): ScreenUnlockCounterDao

    companion object {
        @Volatile
        private var INSTANCE: ScreenUnlockCounterDb? = null

        fun getInstance(context: Context): ScreenUnlockCounterDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScreenUnlockCounterDb::class.java,
                    "one_mb_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}