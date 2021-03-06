package ru.aipova.skintracker.ui.trackpager.track

import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.utils.PhotoFileConstructor
import java.util.*
import javax.inject.Inject

class TrackPresenter @Inject constructor(
    private val trackRepository: TrackRepository,
    private val photoFileConstructor: PhotoFileConstructor
) : TrackContract.Presenter {
    private lateinit var currentDate: Date
    private var trackView: TrackContract.View? = null

    private var existingTrack: Track? = null

    override fun init(date: Date) {
        currentDate = date
        existingTrack = trackRepository.getTrackByDate(currentDate)
        trackView?.setupTrackExists(existingTrack != null || photoExists())
    }

    override fun takeView(view: TrackContract.View) {
        trackView = view
    }

    override fun dropView() {
        trackView = null
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
            trackView?.showNoteView()
            trackView?.setupNoteText(track.note!!)

        }
    }

    override fun onUpdateNote(trackData: TrackData) {
        if (trackData.date == currentDate) {
            trackView?.showNoteView()
            trackView?.setupNoteText(trackData.note)
        }
    }

    private fun loadPhoto() {
        val photoFile = photoFileConstructor.getForDate(currentDate)
        if (photoFile.exists()) {
            trackView?.showPhotoView()
            trackView?.loadPhoto(photoFile)
        }
    }

    private fun photoExists(): Boolean {
        return photoFileConstructor.getForDate(currentDate).exists()
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackView?.showTrackValues(trackValues)
    }
}