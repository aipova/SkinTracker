package ru.aipova.skintracker.ui.trackpager

import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.utils.PhotoFileConstructor
import java.util.*

class TrackPagerPresenter(
    private var trackPagerView: TrackPagerContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository,
    private val photoFileConstructor: PhotoFileConstructor
) : TrackPagerContract.Presenter {
    init {
        trackPagerView.presenter = this
    }
    private var existingTrack: Track? = null

    override fun init() {
        existingTrack = trackRepository.getTrackByDate(currentDate)
        trackPagerView.setupTrackExists(existingTrack != null || photoExists())
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
            trackPagerView.showNoteView()
            trackPagerView.setupNoteText(track.note!!)

        }
    }

    private fun loadPhoto() {
        val photoFile = photoFileConstructor.getForDate(currentDate)
        if (photoFile.exists()) {
            trackPagerView.showPhotoView()
            trackPagerView.loadPhoto(photoFile)
        }
    }

    private fun photoExists(): Boolean {
        return photoFileConstructor.getForDate(currentDate).exists()
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackPagerView.showTrackValues(trackValues)
    }
}