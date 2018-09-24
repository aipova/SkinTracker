package ru.aipova.skintracker.ui.trackvalues

import ru.aipova.skintracker.model.source.TrackRepository
import java.util.*

class TrackValuesPresenter(
    private var trackView: TrackValuesContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository
) : TrackValuesContract.Presenter {
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
                    trackView.showTrackValuesUpdatedMsg()
                } else {
                    trackView.showTrackValuesCreatedMsg()
                }

                trackView.close()
            }

            override fun onError() {
                if (!trackView.isActive) return
                trackView.showCannotSaveTrackValuesMsg()
            }
        })
    }
}