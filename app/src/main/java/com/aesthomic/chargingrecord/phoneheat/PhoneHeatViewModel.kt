package com.aesthomic.chargingrecord.phoneheat

import android.app.Application
import androidx.lifecycle.*
import com.aesthomic.chargingrecord.convertNumericHeatToString
import com.aesthomic.chargingrecord.convertProgressBarToNumericHeat
import com.aesthomic.chargingrecord.database.RecordDao
import kotlinx.coroutines.*

class PhoneHeatViewModel(
    private val recordId: Long,
    private val database: RecordDao,
    application: Application) : AndroidViewModel(application) {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val _navigateToRecord = MutableLiveData<Boolean>()
    val navigateToRecord: LiveData<Boolean>
        get() = _navigateToRecord

    val progressBar = MutableLiveData<Int>()

    init {
        progressBar.value = 50
    }

    fun onSetPhoneHeat(heat: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val record = database.get(recordId) ?: return@withContext
                record.phoneHeat = heat
                database.update(record)
            }

            _navigateToRecord.value = true
        }
    }

    fun onNavigatingDone() {
        _navigateToRecord.value = false
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}