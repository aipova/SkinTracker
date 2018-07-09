package ru.aipova.skintracker.ui.tracktype

import io.realm.RealmResults
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.source.TrackTypeRepository

class TrackTypePresenter(
    private val trackTypeView: TrackTypeContract.View,
    private val trackTypeRepository: TrackTypeRepository
) : TrackTypeContract.Presenter {
    init {
        trackTypeView.presenter = this
    }
    private lateinit var trackTypes: RealmResults<TrackType>

    override fun start() {
        trackTypes = trackTypeRepository.getAllTrackTypesAsync()
        trackTypes.addChangeListener { results ->
            if (results.isLoaded && results.isValid) {
                if (results.isEmpty()) {
                    trackTypeView.showNoTrackTypesView()
                } else {
                    trackTypeView.showTrackTypesView()
                }
            }
        }
        trackTypeView.initTrackTypesView(trackTypes)
    }

    override fun stop() {
        trackTypes.removeAllChangeListeners()
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