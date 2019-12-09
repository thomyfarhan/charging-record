package com.aesthomic.chargingrecord.status

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class StatusViewModelFactory (
    private val application: Application): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StatusViewModel::class.java)) {
            return StatusViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown view model")
    }

}