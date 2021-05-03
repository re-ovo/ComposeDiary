package me.rerere.composediary.util

import java.text.DateFormat
import java.util.*

/**
 * 格式化时间戳
 */
fun Long.formatAsTime(requireClock: Boolean = false): String {
    return if(requireClock) {
        DateFormat.getDateTimeInstance().format(Date(this))
    }else{
        DateFormat.getDateInstance().format(Date(this))
    }
}