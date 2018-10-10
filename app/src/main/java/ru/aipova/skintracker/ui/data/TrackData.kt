package ru.aipova.skintracker.ui.data

import java.util.*

data class TrackData(val date: Date, val note: String = "", val values: Array<TrackValueData>)