package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.utils.PhotoUtils
import java.io.File
import java.util.*

class TrackPresenter(
    private var trackView: TrackContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository,
    private val photoUtils: PhotoUtils
) : TrackContract.Presenter {
    init {
        trackView.presenter = this
    }

    private var trackExists: Boolean = false

    override fun start() {
        loadPhotoIfExists()
        loadTrackValues()
        trackRepository.getTrackByDate(currentDate)?.let {
            trackExists = true
            showTrackNote(it)
        }
    }

    private fun loadPhotoIfExists() {
        val photoFile = getPhotoFile()
        if (photoFile.exists()) {
            trackView.loadPhoto(photoFile)
        }
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackView.showTrackValues(trackValues)
    }

    private fun showTrackNote(existingTrack: Track) {
        existingTrack.note?.let {
            trackView.setupNoteText(it)
        }
    }

    override fun photoCalled() {
        trackView.makePhoto(getPhotoFile())
    }

    override fun photoCreated() {
        loadPhotoIfExists()
    }

    override fun save() {
        val trackData = TrackData(currentDate, trackView.getNote(), trackView.getTrackValueData())
        trackRepository.createOrUpdate(trackData, object : TrackRepository.CreateTrackCallback {
            override fun onTrackCreated() {
                if (!trackView.isActive) return
                if (trackExists) {
                    trackView.showTrackUpdatedMsg()
                } else {
                    trackView.showTrackCreatedMsg()
                }

                trackView.close()
            }

            override fun onError() {
                if (!trackView.isActive) return
                trackView.showCannotCreateTrackMsg()
            }
        })
    }

    private fun getPhotoFile(): File {
        return photoUtils.constructPhotoFile(currentDate)
    }
}