package com.duncan_college.nogrammers_android.events

import java.util.*

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

    fun getStringFromDate (year : Int, month : Int,  dayOfMonth : Int) : String {
        return getMonthFromInt(month + 1) + " " + getDateFromInt(dayOfMonth) + ", " + year
    }

    fun getStringFromTime (hourOfDay : Int, minute : Int, meridianInt : Int = Calendar.AM) : String {
        var meridian = "AM"
        var hour = hourOfDay
        if (hour >= 12) {
            hour -= 12
            meridian = "PM"
        }
        if (meridianInt == Calendar.PM) {
            meridian = "PM"
        }
        if (hour == 0) {
            hour = 12
        }
        return getDateFromInt(hour) + ":" + getDateFromInt(minute) + " $meridian"
    }

    fun getCalendarFromDate (year: Int, month : Int,  dayOfMonth : Int) : Calendar {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.set(year, month, dayOfMonth)
        return cal
    }

    fun getCalendarFromTimeInMillis (timeLong: Long) : Calendar {
        val cal = Calendar.getInstance()
        cal.clear()
        cal.timeInMillis = timeLong
        return cal
    }

    fun getStringFromTimeinMillis (timeLong : Long) : String {
        val time = Calendar.getInstance()
        time.clear()
        time.timeInMillis = timeLong
        return getStringFromTime(time.get(Calendar.HOUR), time.get(Calendar.MINUTE), time.get(Calendar.AM_PM))
    }

    fun getStringFromDateinMillis (dateLong : Long) : String {
        val date = Calendar.getInstance()
        date.clear()
        date.timeInMillis = dateLong
        return getStringFromDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH))
    }
}