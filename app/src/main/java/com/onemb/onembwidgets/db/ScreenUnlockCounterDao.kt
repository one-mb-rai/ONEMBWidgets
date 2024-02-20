package com.onemb.onembwidgets.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface ScreenUnlockCounterDao {
    @Insert
    fun insert(screenUnlockCounter: ScreenUnlockCounterInterface)

    @Query("SELECT * FROM one_mb_db WHERE date = :date")
    fun getCounterByDate(date: String): ScreenUnlockCounterInterface?

    @Query("UPDATE one_mb_db SET counter = :counter WHERE date = :date")
    fun updateCounter(date: String, counter: Int): Int


    @Query("DELETE FROM one_mb_db")
    fun nukeTable()

}