package ru.aipova.skintracker.utils

import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import java.text.DateFormat
import java.util.*

object TimeUtils {
    private val FIRST_DAY = LocalDate(1900, DateTimeConstants.JANUARY, 1)
    private val LAST_DAY = LocalDate(2100, DateTimeConstants.DECEMBER, 31)
    val DAYS_COUNT = Days.daysBetween(
        FIRST_DAY,
        LAST_DAY
    ).days

    fun getDateForPosition(position: Int): Date {
        return FIRST_DAY.withFieldAdded(DurationFieldType.days(), position).toDate()
    }

    fun getCalendarForPosition(position: Int): Calendar {
        return Calendar.getInstance().apply {
            time = getDateForPosition(position)
        }
    }

    fun getPositionForDate(date: LocalDate): Int {
        return Days.daysBetween(FIRST_DAY, date).days
    }

    fun getDateFormatted(position: Int): String? {
        return DateFormat.getDateInstance(DateFormat.LONG).format(
            getDateForPosition(position)
        )
    }

    fun getDateFormatted(date: Date?): String {
        return date?.let { DateFormat.getDateInstance(DateFormat.SHORT).format(date) } ?: ""
    }

    fun today(): LocalDate = LocalDate.now()

    fun todayDate(): Date = LocalDate.now().toDate()

    fun weekAgoDate(): Date = today().minusDays(7).toDate()

    fun monthAgoDate(): Date = today().minusMonths(1).toDate()
}