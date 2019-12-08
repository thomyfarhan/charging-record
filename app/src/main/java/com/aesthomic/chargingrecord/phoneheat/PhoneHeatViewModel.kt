package com.aesthomic.chargingrecord.phoneheat

import androidx.lifecycle.ViewModel
import com.aesthomic.chargingrecord.database.RecordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class PhoneHeatViewModel(
    private val recordId: Long,
    private val database: RecordDao) : ViewModel() {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}