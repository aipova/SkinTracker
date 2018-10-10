package ru.aipova.skintracker.ui.trackvalues

import ru.aipova.skintracker.model.source.TrackRepository
import java.util.*

class TrackValuesPresenter(
    private var trackValuesView: TrackValuesContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository
) : TrackValuesContract.Presenter {
    init {
        trackValuesView.presenter = this
    }

    private var trackExists: Boolean = false

    override fun start() {
        trackValuesView.setTitle(currentDate)
        loadTrackValues()
        trackRepository.getTrackByDate(currentDate)?.let {
            trackExists = true
        }
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackValuesView.showTrackValues(trackValues)
    }

    override fun save() {
        val trackValues =  trackValuesView.getTrackValueData()
        trackRepository.createOrUpdate(currentDate, trackValues, object : TrackRepository.CreateTrackCallback {
            override fun onTrackCreated() {
                if (!trackValuesView.isActive) return
                if (trackExists) {
                    trackValuesView.showTrackValuesUpdatedMsg()
                } else {
                    trackValuesView.showTrackValuesCreatedMsg()
                }

                trackValuesView.close()
            }

            override fun onError() {
                if (!trackValuesView.isActive) return
                trackValuesView.showCannotSaveTrackValuesMsg()
            }
        })
    }
}