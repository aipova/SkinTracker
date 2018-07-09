package ru.aipova.skintracker.ui.tracktype

import ru.aipova.skintracker.model.source.TrackTypeRepository

class TrackTypePresenter(
    private val trackTypeView: TrackTypeContract.View,
    private val trackTypeRepository: TrackTypeRepository
) : TrackTypeContract.Presenter {
    init {
        trackTypeView.presenter = this
    }

    override fun start() {
        trackTypeView.initTrackTypesView(trackTypeRepository.getAllTrackTypesAsync())
    }

    override fun removeTrackType(trackTypeUid: String) {
        trackTypeRepository.removeTrackType(trackTypeUid)
    }

    override fun editTrackTypeName(trackTypeUid: String, trackTypeName: String) {
        trackTypeRepository.editTrackTypeName(trackTypeUid, trackTypeName)
    }

    override fun createNewTrackType(trackTypeName: String) {
        trackTypeRepository.createNewTrackType(trackTypeName,
            object : TrackTypeRepository.CreateTrackTypeCallback {
                override fun onTrackTypeCreated() {
                    trackTypeView.showTrackTypeCreatedMsg(trackTypeName)
                }

                override fun onError() {
                    trackTypeView.showCannotCreateTrackTypeMsg()
                }
            })
    }
}