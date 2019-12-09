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
     * has been pressed or not
     */
    private val _eventStart = MutableLiveData<Boolean>()
    val eventStart: LiveData<Boolean>
        get() = _eventStart

    /**
     * This variable gives a state whether the stop button
     * has been pressed or not
     */
    private val _eventStop = MutableLiveData<Boolean>()
    val eventStop: LiveData<Boolean>
        get() = _eventStop

    private val _eventClear = MutableLiveData<Boolean>()
    val eventClear: LiveData<Boolean>
        get() = _eventClear

    private val _eventSnackBar = MutableLiveData<Boolean>()
    val eventSnackBar: LiveData<Boolean>
        get() = _eventSnackBar

    private val _navigateToPhoneHeat = MutableLiveData<Record?>()
    val navigateToPhoneHeat: LiveData<Record?>
        get() = _navigateToPhoneHeat

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

    val startButtonVisibility = Transformations.map(currentRecord) {
        it == null
    }

    val stopButtonVisibility = Transformations.map(currentRecord) {
        it != null
    }

    val deleteButtonVisibility = Transformations.map(records) {
        it.isNotEmpty()
    }

    init {
        _eventStart.value = false
        _eventStop.value = false
        _navigateToPhoneHeat.value = null
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
     * Update data to database using IO Thread
     * update function will automatically search the record
     * with the same id
     */
    private suspend fun update(record: Record) {
        withContext(Dispatchers.IO) {
            database.update(record)
        }
    }

    /**
     * Clear all rows from database
     */
    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
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
     * Update record object on database
     * syntax @launch in return@launch specifies the function
     * from which this statement returns, among several nested functions.
     * When we do the return inside the function,
     * the function will automatically stop processing the next block of codes
     */
    fun onStopCharging(level: Int) {
        uiScope.launch {
            val oldRecord = currentRecord.value ?: return@launch
            oldRecord.endTimeMilli = System.currentTimeMillis()
            oldRecord.endBatteryLevel = level
            update(oldRecord)

            _eventStop.value = false
            _navigateToPhoneHeat.value = oldRecord
        }
    }

    /**
     * Clear all record rows from database
     * and set currentRecord to null
     */
    fun onClear() {
        uiScope.launch {
            clear()
            currentRecord.value = null
            _eventSnackBar.value = true
            _eventClear.value = false
        }
    }

    /**
     * When start button pressed eventStart variable will be changed
     */
    fun onEventStart() {
        _eventStart.value = true
    }

    /**
     * When stop button pressed eventStop variable will be changed
     */
    fun onEventStop() {
        _eventStop.value = true
    }

    fun onEventClear() {
        _eventClear.value = true
    }

    fun onNavigatingDone() {
        _navigateToPhoneHeat.value = null
    }

    fun onSnackbarDone() {
        _eventSnackBar.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}