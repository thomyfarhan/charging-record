package com.aesthomic.chargingrecord.database

data class Record(
    var id: Long = 0L,
    val startTimeMilli: Long = 0L,
    var endTimeMilli: Long = startTimeMilli,
    var startBatteryLevel: Int = 0,
    var endBatteryLevel: Int = startBatteryLevel,
    var phoneHeat: Int = -1
)
