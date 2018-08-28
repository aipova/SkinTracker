package ru.aipova.skintracker.utils

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoUtils(private val storageDir: File) {

    fun constructPhotoFile(date: Date): File {
        return File(storageDir, constructPhotoFileName(date))
    }

    fun constructPhotoFileName(date: Date): String {
        val timeStamp = SimpleDateFormat(DATE_STAMP_FORMAT).format(date)
        return "ST_$timeStamp.jpg"
    }

    companion object {
        const val DATE_STAMP_FORMAT = "yyyyMMdd"
    }
}
