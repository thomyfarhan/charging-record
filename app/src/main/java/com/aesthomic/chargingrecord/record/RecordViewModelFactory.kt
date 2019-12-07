package com.aesthomic.chargingrecord.record

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aesthomic.chargingrecord.database.RecordDao
import java.lang.IllegalArgumentException

/**
 * This class take same parameter as
 * the RecordViewModel
 *
 * ViewModelFactory helps you when you need to pass
 * parameters to the ViewModel
 */
class RecordViewModelFactory(
    private val database: RecordDao,
    private val application: Application): ViewModelProvider.Factory {

    /**
     * Check if there is a RecordViewModel class available
     * and if there is, returns an instance of it
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecordViewModel::class.java)){
            return RecordViewModel(database, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
