package com.aesthomic.chargingrecord.phoneheat

import android.app.Application
import androidx.lifecycle.*
import com.aesthomic.chargingrecord.convertNumericHeatToString
import com.aesthomic.chargingrecord.convertProgressBarToNumericHeat
import com.aesthomic.chargingrecord.database.RecordDao
import com.aesthomic.chargingrecord.getBatteryLevel
import kotlinx.coroutines.*

class PhoneHeatViewModel(
    private val recordId: Long,
    private val database: RecordDao,
    application: Application) : AndroidViewModel(application) {

    /**
     * Some variables inside a function can't access
     * application constructor, that's why i create this variable
     * in the first place to make application accessible inside the ViewModel
     */
    private val app = application

    /**
     * This job has a relation to Coroutine, it can cancel
     * or stop the Coroutine if its not needed anymore by this ViewModel
     */
    private val viewModelJob = Job()

    /**
     * uiScope contain a scope to determines which Thread this Coroutine
     * will run on
     * This uiScope will run on Main Thread
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * This encapsulated live data will become the guide for the fragment
     * to navigate to the Record Fragment
     */
    private val _navigateToRecord = MutableLiveData<Boolean>()
    val navigateToRecord: LiveData<Boolean>
        get() = _navigateToRecord

    /**
     * Contains a value for the state of seek bar progress
     */
    val progressBar = MutableLiveData<Int>()

    /**
     * Transform the progress value to the numeric range
     * from 0 to 5
     */
    val heatNumeric = Transformations.map(progressBar) {
        convertProgressBarToNumericHeat(it)
    }

    /**
     * Transform the numeric value of heat to the string
     */
    val heatString = Transformations.map(heatNumeric) {
        convertNumericHeatToString(it, application.resources)
    }

    init {
        progressBar.value = 50
    }

    /**
     * syntax @withContext in return@withContext specifies the function
     * from which this statement returns, among several nested functions.
     * When we do the return inside the function,
     * the function will automatically stop processing the next block of codes
     *
     * @param heat: Value of heat range from 0 .. 5
     */
    fun onSetPhoneHeat(heat: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val record = database.get(recordId) ?: return@withContext

                record.apply {
                    endBatteryLevel = getBatteryLevel(app)
                    endTimeMilli = System.currentTimeMillis()
                    phoneHeat = heat
                }

                /**
                 * Update data to database using IO Thread
                 * update function will automatically search the record
                 * with the same id
                 */
                database.update(record)
            }
            _navigateToRecord.value = true
        }
    }

    /**
     * This function will be called when navigating to Record Fragment
     * is done
     */
    fun onNavigatingDone() {
        _navigateToRecord.value = false
    }

    /**
     * on This onCleared override function, this ViewModel job will be called
     * to cancel all job operation
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}