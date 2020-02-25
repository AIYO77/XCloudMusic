package com.xw.lib_common.ext

import android.icu.util.Calendar
import com.orhanobut.logger.Logger
import com.xw.lib_common.R
import com.xw.lib_common.base.BaseApplication
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author: xingwei
 * @email: 654206017@qq.com
 *
 * Desc:
 */
//12:23
fun Long.makeTimeString(): String {
    val sb = StringBuffer()
    val m = this / (60 * 1000)
    sb.append(if (m < 10) "0$m" else m)
    sb.append(":")
    val s = this % (60 * 1000) / 1000
    sb.append(if (s < 10) "0$s" else s)
    return sb.toString()
}

fun Long.makeShortTimeString(): String {
    var secs = this
    val hours: Long
    val mins: Long

    hours = secs / 3600
    secs %= 3600
    mins = secs / 60
    secs %= 60

    val durationFormat = BaseApplication.CONTEXT.resources
        .getString(if (hours == 0L) R.string.durationformatshort else R.string.durationformatlong)
    return String.format(durationFormat, hours, mins, secs)
}

fun Int.makeTimeString(): String {
    val sb = StringBuffer()
    val m = this / (60 * 1000)
    sb.append(if (m < 10) "0$m" else m)
    sb.append(":")
    val s = this % (60 * 1000) / 1000
    sb.append(if (s < 10) "0$s" else s)
    return sb.toString()
}

fun getCurTime(): String {
    return System.currentTimeMillis().toString()
}

/**
 * 两个时间相差多少秒
 */
fun differentBetweenTime(time1: Long, time2: Long): Long {
    return ((time2 - time1) / (1000))
}

/**
 * return 几号
 */
fun getDay(): String {
    return if (fromN()) {
        Calendar.getInstance().get(Calendar.DAY_OF_MONTH).toString()
    } else {
        val simpleDateFormat = SimpleDateFormat("dd", Locale.CHINA)
        val date = Date(System.currentTimeMillis())
        simpleDateFormat.format(date)
    }
}

/**
 * return 几月
 */
fun getMonth(): String {
    return if (fromN()) {
        Calendar.getInstance().get(Calendar.MONTH).toString()
    } else {
        val simpleDateFormat = SimpleDateFormat("MM", Locale.CHINA)
        val date = Date(System.currentTimeMillis())
        simpleDateFormat.format(date)
    }
}

/**
 * return 04/01 几号/几月
 */
fun getMmAndDd(): String {
    return "${getDay()}/${getMonth()}"
}

fun Long.format(format: String): String {
    return try {
        val simpleDateFormat = SimpleDateFormat(format, Locale.CHINA)
        val date = Date(this)
        simpleDateFormat.format(date)
    } catch (e: Exception) {
        Logger.e(e.localizedMessage)
        ""
    }

}