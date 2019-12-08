package com.aesthomic.chargingrecord.phoneheat

import androidx.lifecycle.ViewModel
import com.aesthomic.chargingrecord.database.RecordDao

class PhoneHeatViewModel(
    private val recordId: Long,
    private val database: RecordDao) : ViewModel() {}