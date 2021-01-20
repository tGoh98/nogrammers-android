package com.example.nogrammers_android.events

import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.Month

object DateTimeUtil {
    val intToMonth = mapOf<Int, String>(Pair(1, "Jan"), Pair(2, "Feb"), Pair(3, "Mar"),
            Pair(4, "Apr"), Pair(5, "May"), Pair(6, "June"), Pair(7, "July"), Pair(8, "Aug"),
            Pair(9, "Sept"), Pair(10, "Oct"), Pair(11, "Nov"), Pair(12, "Dec"))

    fun getMonthFromInt (month: Int): String {
        return if (intToMonth.containsKey(month)) {
            intToMonth.get(month)!!
        }
        else {
            "null"
        }
    }

    fun getDateFromInt (date: Int): String {
        return if (date < 10) {
            "0$date"
        }
        else {
            date.toString()
        }
    }

    fun getStringFromDate (month : Int,  dayOfMonth : Int) : String {
         return getMonthFromInt(month + 1) + " " + getDateFromInt(dayOfMonth)
    }

    fun getStringFromTime (hourOfDay : Int, minute : Int) : String {
        var meridian = "AM"
        var hour = hourOfDay
        if (hour >= 12) {
            hour -= 12
            meridian = "PM"
        }
        if (hour == 0) {
            hour = 12
        }
        return getDateFromInt(hour) + ":" + getDateFromInt(minute) + " $meridian"
    }
}