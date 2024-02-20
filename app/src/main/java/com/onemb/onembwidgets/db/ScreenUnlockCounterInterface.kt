package com.onemb.onembwidgets.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "one_mb_db")
data class ScreenUnlockCounterInterface(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val counter: Int
)