package ru.aipova.skintracker.model
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Track : RealmObject() {

    @PrimaryKey
    var uuid: String = UUID.randomUUID().toString()
    var values: RealmList<TrackValue> = RealmList()
    var note: String? = null
    var date: Date? = null
}