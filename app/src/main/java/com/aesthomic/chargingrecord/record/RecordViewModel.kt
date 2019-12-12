package com.aesthomic.chargingrecord.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aesthomic.chargingrecord.database.Record
import com.aesthomic.chargingrecord.database.RecordDao
import com.aesthomic.chargingrecord.formatRecords
import com.aesthomic.chargingrecord.getBatteryLevel
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
     * Enabling to use application inside function in this ViewModel
     * this technique may not be the best practice, and i still yet to know
     * the proper technique to place the application
     */
    private val app = application

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
     * This variable gives a state whether the clear button
     * has been pressed or not
     */
    private val _eventClear = MutableLiveData<Boolean>()
    val eventClear: LiveData<Boolean>
        get() = _eventClear

    /**
     * Guide for the view whether the snackbar will be performed or not
     */
    private val _eventSnackBar = MutableLiveData<Boolean>()
    val eventSnackBar: LiveData<Boolean>
        get() = _eventSnackBar

    /**
     * A place for determine the state to navigate
     * to Phone Heat Fragment
     */
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
        formatRecords(records, app.resources)
    }

    /**
     * Enable the Start Button if currentRecord is null
     */
    val startButtonVisibility = Transformations.map(currentRecord) {
        it == null
    }

    /**
     * Enable the Stop Button if currentRecord has data
     */
    val stopButtonVisibility = Transformations.map(currentRecord) {
        it != null
    }

    /**
     * Disable the Delete Button if the records don't have
     * any data or row
     */
    val deleteButtonVisibility = Transformations.map(records) {
        it.isNotEmpty()
    }

    init {
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
    fun onStartCharging() {
        uiScope.launch {
            val level = getBatteryLevel(app)
            val record = Record(
                startTimeMilli = System.currentTimeMillis(),
                startBatteryLevel = level
            )
            insert(record)
            currentRecord.value = getRecordFromDatabase()
        }
    }

    fun onStopCharging() {
        _navigateToPhoneHeat.value = currentRecord.value ?: return
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