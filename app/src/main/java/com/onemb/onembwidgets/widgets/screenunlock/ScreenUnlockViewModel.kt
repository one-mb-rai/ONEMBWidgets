package com.onemb.onembwidgets.widgets.screenunlock

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onemb.onembwidgets.db.ScreenUnlockCounterDb
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ScreenUnlockViewModel: ViewModel() {


    fun getScreenUnlockCounters(context: Context) {
//        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        val database = ScreenUnlockCounterDb.getInstance(context)
//        val counterDao = database.screenUnlockCounterDao()
//        return counterDao.getCounterByDate(currentDate)
    }

}