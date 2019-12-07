package com.aesthomic.chargingrecord.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * A Database that stores Charging Record Information
 * And global method to get access to the database
 *
 * you'll have to increase the version if schema has been changed
 */
@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class ChargingRecordDatabase: RoomDatabase() {}
