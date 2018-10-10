package ru.aipova.skintracker.model
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class TrackValue : RealmObject() {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var trackType: TrackType? = null
    var value: Long? = null
}