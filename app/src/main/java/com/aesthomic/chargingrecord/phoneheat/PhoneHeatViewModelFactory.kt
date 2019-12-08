package com.aesthomic.chargingrecord.phoneheat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aesthomic.chargingrecord.database.RecordDao
import java.lang.IllegalArgumentException

class PhoneHeatViewModelFactory (
    private val recordId: Long,
    private val database: RecordDao) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhoneHeatViewModel::class.java)) {
            return PhoneHeatViewModel(recordId, database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}