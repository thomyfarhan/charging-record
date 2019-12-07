package com.aesthomic.chargingrecord.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aesthomic.chargingrecord.database.Record
import com.aesthomic.chargingrecord.database.RecordDao
import kotlinx.coroutines.*

/**
 * AndroidViewModel is the same class as ViewModel
 * it takes application context as parameter
 * and makes it available as a property
 */
class RecordViewModel(
    val database: RecordDao,
    application: Application) : AndroidViewModel(application) {

    /**
     * This variable allows to cancel all coroutines started by this view model
     * When view model is no longer used or destroyed
     */
    private val viewModelJob = Job()

    /**
     * This scope determines which thread this coroutine will run on
     * uiScope will run on Main thread (Dispatcher.Main)
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var currentRecord = MutableLiveData<Record>()

    private val _eventStart = MutableLiveData<Boolean>()
    val eventStart: LiveData<Boolean>
        get() = _eventStart

    init {
        _eventStart.value = false
        initializeCurrentRecord()
    }

    private fun initializeCurrentRecord() {
        uiScope.launch {
            currentRecord.value = getRecordFromDatabase()
        }
    }

    /**
     * suspend function to do a running work
     * so the UI thread won't get blocked while waiting for result
     */
    private suspend fun getRecordFromDatabase(): Record? {
        /**
         * Use Dispatcher.IO as thread
         * because getting data from database is I/O Operation
         */
        return withContext(Dispatchers.IO) {
            var record = database.getNewest()
            if (record?.startTimeMilli != record?.endTimeMilli) {
                record = null
            }
            record
        }
    }

    private suspend fun insert(record: Record) {
        withContext(Dispatchers.IO) {
            database.insert(record)
        }
    }

    fun onStartCharging(level: Int) {
        uiScope.launch {
            val record = Record(
                startTimeMilli = System.currentTimeMillis(),
                startBatteryLevel = level
            )
            insert(record)
            currentRecord.value = getRecordFromDatabase()
        }
        _eventStart.value = false
    }

    fun onEventStart() {
        _eventStart.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}