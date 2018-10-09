package ru.aipova.skintracker.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable
import java.util.*

open class TrackType : RealmObject(), Serializable {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var removable: Boolean = true
    var name: String = ""
    var valueType: String = ValueType.SEEK.name
    var maxValue: Long = 10L
    var minValue: Long = 0L

    fun setValueTypeEnum(valueType: ValueType) {
        this.valueType = valueType.name
    }

    fun getValueTypeEnum(): ValueType {
        return ValueType.valueOf(valueType)
    }

    companion object {
        const val SKIN_QUALITY_TRACK_TYPE_UID = "skin-quality"
        const val PIMPLES_COUNT_TRACK_TYPE_UID = "pimples-amount"
        const val SUGAR_TRACK_TYPE_UID = "sugar"
        const val MAKEUP_TRACK_TYPE_UID = "pimples-picking"
    }
}