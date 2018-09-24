package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.model.source.TrackRepository
import java.util.*

class TrackPresenter(
    private var trackView: TrackContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository
) : TrackContract.Presenter {
    init {
        trackView.presenter = this
    }

    private var trackExists: Boolean = false

    override fun start() {
        trackView.setTitle(currentDate)
        loadTrackValues()
        trackRepository.getTrackByDate(currentDate)?.let {
            trackExists = true
        }
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackView.showTrackValues(trackValues)
    }

    override fun save() {
        val trackValues =  trackView.getTrackValueData()
        trackRepository.createOrUpdate(currentDate, trackValues, object : TrackRepository.CreateTrackCallback {
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
}