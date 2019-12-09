package com.aesthomic.chargingrecord.status

import android.app.Application
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.aesthomic.chargingrecord.getBatteryStatus
import com.aesthomic.chargingrecord.getChargingStatus

class StatusViewModel(
    application: Application): AndroidViewModel(application) {

    private val _level = MutableLiveData<Int>()
    val level: LiveData<Int>
        get() = _level

    private val _isCharging = MutableLiveData<Boolean>()
    val isCharging: LiveData<Boolean>
        get() = _isCharging

    val chargingInfo = Transformations.map(isCharging) {
        getChargingStatus(it, application.resources)
    }

    init {
        initStatus(application)
    }

    private fun initStatus(application: Application) {
        _level.value = getBatteryStatus(application)?.
            getIntExtra(BatteryManager.EXTRA_LEVEL, -1)

        val status = getBatteryStatus(application)?.
            getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        _isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

    }

}
