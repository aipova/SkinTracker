package ru.aipova.skintracker.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoUtils {

    companion object {
        fun constructPhotoFile(date: Date, context: Context): File {
            val timeStamp = SimpleDateFormat("yyyyMMdd").format(date)
            val imageFileName = "ST_$timeStamp.jpg"
            val storageDir = context.getExternalFilesDir("photos")
            return File(storageDir, imageFileName)
        }
    }
}
