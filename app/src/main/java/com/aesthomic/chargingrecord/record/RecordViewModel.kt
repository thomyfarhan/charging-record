package com.aesthomic.chargingrecord.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.aesthomic.chargingrecord.database.RecordDao

/**
 * AndroidViewModel is the same class as ViewModel
 * it takes application context as parameter
 * and makes it available as a property
 */
class RecordViewModel(
    val database: RecordDao,
    application: Application) : AndroidViewModel(application) {

}