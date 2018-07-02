package ru.aipova.skintracker.model
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Track : RealmObject() {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var trackType: TrackType? = null
    var value: Long? = null
    var date: Date? = null
}