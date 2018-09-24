package ru.aipova.skintracker.ui.trackpager.track

import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.utils.PhotoFileConstructor
import java.util.*

class TrackPresenter(
    private var trackView: TrackContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository,
    private val photoFileConstructor: PhotoFileConstructor
) : TrackContract.Presenter {
    init {
        trackView.presenter = this
    }
    private var existingTrack: Track? = null

    override fun init() {
        existingTrack = trackRepository.getTrackByDate(currentDate)
        trackView.setupTrackExists(existingTrack != null || photoExists())
    }

    override fun start() {
        loadPhoto()
        existingTrack?.run {
            loadNote(this)
            if (values.isNotEmpty()) loadTrackValues()
        }
    }

    private fun loadNote(track: Track) {
        if (!track.note.isNullOrBlank()) {
            trackView.showNoteView()
            trackView.setupNoteText(track.note!!)

        }
    }

    private fun loadPhoto() {
        val photoFile = photoFileConstructor.getForDate(currentDate)
        if (photoFile.exists()) {
            trackView.showPhotoView()
            trackView.loadPhoto(photoFile)
        }
    }

    private fun photoExists(): Boolean {
        return photoFileConstructor.getForDate(currentDate).exists()
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackView.showTrackValues(trackValues)
    }
}