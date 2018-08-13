package ru.aipova.skintracker.ui.track

import java.util.*

data class TrackData(val date: Date, val note: String, val values: Array<TrackValueData>)