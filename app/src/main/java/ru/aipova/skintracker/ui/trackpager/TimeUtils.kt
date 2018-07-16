package ru.aipova.skintracker.ui.trackpager

import android.content.Context
import android.text.format.DateFormat
import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import java.util.*

class TimeUtils {

    companion object {
        private val FIRST_DAY = LocalDate(1900, DateTimeConstants.JANUARY, 1)
        private val LAST_DAY = LocalDate(2100, DateTimeConstants.DECEMBER, 31)
        val DAYS_COUNT = Days.daysBetween(FIRST_DAY, LAST_DAY).days

        fun getDateForPosition(position: Int): Date {
            return FIRST_DAY.withFieldAdded(DurationFieldType.days(), position).toDate()
        }

        fun getPositionForDate(date: LocalDate): Int {
            return Days.daysBetween(FIRST_DAY, date).days
        }

        fun getDateFormatted(context: Context, position: Int): String? {
            return DateFormat.getDateFormat(context).format(getDateForPosition(position))
        }

        fun today() = LocalDate.now()

    }
}