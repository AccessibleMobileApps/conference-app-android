package com.robinkanatzar.conference.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    fun toScheduleHeaderFormat(date: Date) : String {
        val format = SimpleDateFormat("dd MMM yyyy")
        return format.format(date)
    }

    fun toScheduleCellFormat(date: Date) : String {
        val format = SimpleDateFormat("hh:mm aa")
        return format.format(date)
    }
}