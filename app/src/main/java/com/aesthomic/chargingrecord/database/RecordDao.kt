package com.aesthomic.chargingrecord.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Interface with some functions for accessing database with query
 */
@Dao
interface RecordDao {

    /**
     * Create new row to the database
     *
     * @param record new value to create
     */
    @Insert
    fun insert(record: Record)

    /**
     * When a row with same id already existed
     * the old row will replaced with the new one
     *
     * @param record new value to replace the old one
     */
    @Update
    fun update(record: Record)

    /**
     * Fetch record value that matches with id which is key param
     *
     * @param key value to find match row
     */
    @Query("SELECT * FROM record_table WHERE id = :key")
    fun get(key: Long): Record?

    /**
     * Fetch all rows ordered by the newest row
     * And return the list with form of LiveData
     */
    @Query("SELECT * FROM record_table ORDER BY id DESC")
    fun getAllRecords(): LiveData<List<Record>>

    /**
     * Fetch row with the highest id
     * which is the newest one that has been added
     */
    @Query("SELECT * FROM record_table ORDER BY id DESC LIMIT 1")
    fun getNewest(): Record?

    /**
     * Delete all rows from database
     * Table will not be deleted, only the rows
     */
    @Query("DELETE FROM record_table")
    fun clear()

}
