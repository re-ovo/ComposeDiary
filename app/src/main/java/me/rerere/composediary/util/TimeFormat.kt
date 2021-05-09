package me.rerere.composediary.util

import java.text.DateFormat
import java.util.*

/**
 * 格式化时间戳
 * Format time
 */
fun Long.formatAsTime(requireClock: Boolean = false): String {
    return if(requireClock) {
        DateFormat.getDateTimeInstance().format(Date(this))
    }else{
        DateFormat.getDateInstance(2, Locale.getDefault()).format(Date(this))
    }
}