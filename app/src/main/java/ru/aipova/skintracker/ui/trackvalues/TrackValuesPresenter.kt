package ru.aipova.skintracker.ui.trackvalues

import ru.aipova.skintracker.model.source.TrackRepository
import java.util.*
import javax.inject.Inject

class TrackValuesPresenter @Inject constructor(
    private val trackRepository: TrackRepository
) : TrackValuesContract.Presenter {
    private var trackValuesView: TrackValuesContract.View? = null
    private var trackExists: Boolean = false
    private var currentDate: Date = Date()

    override fun takeView(view: TrackValuesContract.View) {
        trackValuesView = view
    }

    override fun dropView() {
        trackValuesView = null
    }

    override fun init(date: Date) {
        currentDate = date
    }

    override fun start() {
        trackValuesView?.setTitle(currentDate)
        loadTrackValues()
        trackRepository.getTrackByDate(currentDate)?.let {
            trackExists = true
        }
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackValuesView?.showTrackValues(trackValues)
    }

    override fun save() {
        val trackValues =  trackValuesView?.getTrackValueData() ?: return
        trackRepository.createOrUpdate(currentDate, trackValues, object : TrackRepository.CreateTrackCallback {
            override fun onTrackCreated() {
                trackValuesView?.run {
                    if (trackExists) {
                        showTrackValuesUpdatedMsg()
                    } else {
                        showTrackValuesCreatedMsg()
                    }
                    close()
                }
            }

            override fun onError() {
                trackValuesView?.showCannotSaveTrackValuesMsg()
            }
        })
    }
}