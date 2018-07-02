package ru.aipova.skintracker.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class TrackType : RealmObject() {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var removable: Boolean = true
    var name: String? = null
    var removed: Boolean = false

    companion object {
        public const val SKIN_QUALITY_TRACK_TYPE_UID = "skin-quality"
        public const val SKIN_QUALITY_TRACK_NAME = "Skin Quality"
    }
}