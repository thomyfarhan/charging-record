package com.aesthomic.chargingrecord.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aesthomic.chargingrecord.database.Record
import com.aesthomic.chargingrecord.database.RecordDao
import com.aesthomic.chargingrecord.formatRecords
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

    /**
     * Holds current Record data that has already been added
     */
    private var currentRecord = MutableLiveData<Record>()

    /**
     * This variable gives a state whether the start button
     * has pressed or not
     */
    private val _eventStart = MutableLiveData<Boolean>()
    val eventStart: LiveData<Boolean>
        get() = _eventStart

    /**
     * get all record rows from database and put it on variable
     * We do not need the coroutine to fetch data from database
     * because get function in DAO already returned a LiveData Object
     */
    private val records = database.getAllRecords()

    /**
     * Format the records list in LiveData to the strings
     * using Transformation.map
     */
    val recordsString = Transformations.map(records) { records ->
        formatRecords(records, application.resources)
    }

    init {
        _eventStart.value = false
        initializeCurrentRecord()
    }

    /**
     * get the current Record data from the database
     */
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

    /**
     * Insert data to database using IO Thread
     */
    private suspend fun insert(record: Record) {
        withContext(Dispatchers.IO) {
            database.insert(record)
        }
    }

    /**
     * Create Record object and add it to database
     * After all actions done, eventStart variable
     * back to the initial state
     */
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

    /**
     * When start button pressed eventStart variable will changed
     */
    fun onEventStart() {
        _eventStart.value = true
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}