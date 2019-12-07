package com.aesthomic.chargingrecord.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "record_table")
data class Record(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "startTimeMilli")
    val startTimeMilli: Long = 0L,

    @ColumnInfo(name = "endTimeMilli")
    var endTimeMilli: Long = startTimeMilli,

    @ColumnInfo(name = "startBatteryLevel")
    var startBatteryLevel: Int = 0,

    @ColumnInfo(name = "endBatteryLevel")
    var endBatteryLevel: Int = startBatteryLevel,

    @ColumnInfo(name = "phoneHeat")
    var phoneHeat: Int = -1
)
