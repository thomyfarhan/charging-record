package com.aesthomic.chargingrecord

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.os.BatteryManager
import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.aesthomic.chargingrecord.database.Record
import java.text.SimpleDateFormat

/**
 * Build a string with a data resource from records
 * return with HTML Format in one string form
 *
 * @param records: List of all records in database
 * @param resources: resources object for all resources in app
 *
 * @return Spanned: an Interface for text that has formatting attached to it
 */
fun formatRecords(records: List<Record>, resources: Resources): Spanned {
    val sb = StringBuilder()
    sb.apply {
        append(resources.getString(R.string.list_title))
        records.forEach {
            append("<br>")
            append(resources.getString(R.string.list_start_time))
            append("\t${convertLongToDateString(it.startTimeMilli)}<br>")
            if (it.endTimeMilli != it.startTimeMilli) {
                append(resources.getString(R.string.list_end_time))
                append("\t${convertLongToDateString(it.endTimeMilli)}<br>")
                append(resources.getString(R.string.list_total_charge))
                append("\t${it.endBatteryLevel - it.startBatteryLevel}<br>")
                append(resources.getString(R.string.list_phone_heat))
                append("\t${convertNumericHeatToString(it.phoneHeat, resources)}<br>")
                append(resources.getString(R.string.list_hours))
                // Hours
                append("\t ${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60 / 60}:")
                // Minutes
                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000 / 60}:")
                // Seconds
                append("${it.endTimeMilli.minus(it.startTimeMilli) / 1000}<br><br>")
            }
        }
    }
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
    } else {
        HtmlCompat.fromHtml(sb.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}

/**
 * Convert numeric phoneHeat to string format
 *
 * @param phoneHeat: phoneHeat in type of int
 * @param resources: resources object in app
 *
 * @return String: phoneHeat result formatted in string
 */
fun convertNumericHeatToString(phoneHeat: Int, resources: Resources): String {
    return when(phoneHeat) {
        0 -> resources.getString(R.string.zero_very_hot)
        1 -> resources.getString(R.string.one_hot)
        2 -> resources.getString(R.string.two_so_so)
        3 -> resources.getString(R.string.three_ok)
        4 -> resources.getString(R.string.four_stable)
        5 -> resources.getString(R.string.five_very_stable)
        else -> "--"
    }
}

/**
 * Format time in millis to the appropriate format
 * in this function we change the format to this example
 * Sunday, Dec-31-2020 Time: 19:07
 *
 * @param time - time in millis that will be formatted
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(time: Long): String {
    return SimpleDateFormat("EEEE', 'MMM-dd-yyyy' Time: 'HH:mm")
        .format(time).toString()
}


fun getBatteryStatus(application: Application): Intent? {
    val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    return application.registerReceiver(null, ifilter)
}

fun getChargingStatus(isCharging: Boolean, resources: Resources): String {
    return if (isCharging) resources.getString(R.string.in_charging) 
        else resources.getString(R.string.not_charging)
}

fun convertProgressBarToNumericHeat(value: Int): Int {
    return when(value) {
        in 0..15 -> 0
        in 16..30 -> 1
        in 31..45 -> 2
        in 46..60 -> 3
        in 61..75 -> 4
        in 76..90 -> 5
        else -> -1
    }
}

fun getBatteryLevel(application: Application): Int {
    return getBatteryStatus(application)?.
        getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
}