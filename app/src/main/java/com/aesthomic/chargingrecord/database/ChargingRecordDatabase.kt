package com.aesthomic.chargingrecord.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * A Database that stores Charging Record Information
 * And global method to get access to the database
 *
 * you'll have to increase the version if schema has been changed
 */
@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class ChargingRecordDatabase: RoomDatabase() {

    /**
     * Database needs to know the DAO
     * DAO could be multiple
     */
    abstract val recordDao: RecordDao

    companion object {

        /**
         * This variable will keep a reference to the database
         * It will helps you avoid repeatedly opening connection to database
         */
        @Volatile
        private var INSTANCE: ChargingRecordDatabase? = null

        /**
         * Get the Database
         * if database has already been retrieved
         * the previous database will be returned
         * Otherwise create new database
         */
        fun getInstance(context: Context): ChargingRecordDatabase {

            /**
             * Wrapping code into the synchronized
             * will limit to one thread execution at a time
             * to enter this block of code
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    /**
                     * if instance is null,
                     * use databaseBuilder to get a database
                     */
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChargingRecordDatabase::class.java,
                        "charging_record_database"
                    )
                        /**
                         * Destroy and rebuild database
                         * if schema had changed
                         */
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}
