package ru.aipova.skintracker.ui.trackpager

import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.utils.PhotoUtils
import java.util.*

class TrackPagerPresenter(
    private var trackPagerView: TrackPagerContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository,
    private val photoUtils: PhotoUtils
) : TrackPagerContract.Presenter {
    init {
        trackPagerView.presenter = this
    }
    private var existingTrack: Track? = null

    override fun init() {
        existingTrack = trackRepository.getTrackByDate(currentDate)
        trackPagerView.setupTrackExists(existingTrack != null)
    }

    override fun start() {
        existingTrack?.run {
            loadNote(this)
            loadPhoto()
            loadTrackValues()
        }
    }

    private fun loadNote(track: Track) {
        if (!track.note.isNullOrBlank()) {
            trackPagerView.showNoteView()
            trackPagerView.setupNoteText(track.note!!)

        }
    }

    private fun loadPhoto() {
        val photoFile = photoUtils.constructPhotoFile(currentDate)
        if (photoFile.exists()) {
            trackPagerView.showPhotoView()
            trackPagerView.loadPhoto(photoFile)
        } else {
            trackPagerView.hidePhotoView()
        }
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackPagerView.showTrackValues(trackValues)
    }
}