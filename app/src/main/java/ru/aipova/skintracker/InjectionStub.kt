package ru.aipova.skintracker

import io.realm.Realm
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackTypeRepository
import ru.aipova.skintracker.utils.PhotoFileConstructor

// TODO it's a stub before appropriate architecture decision
object InjectionStub {
    lateinit var realm: Realm
    lateinit var trackTypeRepository: TrackTypeRepository
    lateinit var trackRepository: TrackRepository
    lateinit var photoFileConstructor: PhotoFileConstructor
}