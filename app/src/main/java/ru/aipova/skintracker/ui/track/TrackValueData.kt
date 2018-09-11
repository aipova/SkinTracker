package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.model.ValueType

data class TrackValueData(val uid: String, val name: String, var type: ValueType, var value: Int, var max: Int = 10)