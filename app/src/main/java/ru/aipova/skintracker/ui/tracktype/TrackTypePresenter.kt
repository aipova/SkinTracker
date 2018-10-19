package ru.aipova.skintracker.ui.tracktype

import io.realm.RealmResults
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.ValueType
import ru.aipova.skintracker.model.source.TrackTypeRepository
import javax.inject.Inject

class TrackTypePresenter @Inject constructor(
    private val trackTypeRepository: TrackTypeRepository
) : TrackTypeContract.Presenter {
    private var trackTypeView: TrackTypeContract.View? = null

    override fun takeView(view: TrackTypeContract.View) {
        trackTypeView = view
    }

    override fun dropView() {
        trackTypeView = null
    }

    private lateinit var trackTypes: RealmResults<TrackType>

    override fun start() {
        trackTypes = trackTypeRepository.getEditableTrackTypesAsync()
        trackTypes.addChangeListener { results ->
            if (results.isLoaded && results.isValid) {
                if (results.isEmpty()) {
                    trackTypeView?.showNoTrackTypesView()
                } else {
                    trackTypeView?.showTrackTypesView()
                }
            }
        }
        trackTypeView?.initTrackTypesView(trackTypes)
    }

    override fun stop() {
        trackTypes.removeAllChangeListeners()
    }

    override fun removeTrackType(trackTypeUid: String) {
        trackTypeRepository.removeTrackType(trackTypeUid)
    }

    override fun editTrackTypeName(
        trackTypeUid: String,
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    ) {
        trackTypeRepository.editTrackType(trackTypeUid, trackTypeName, selectedValueType, min, max)
    }

    override fun createNewTrackType(
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    ) {
        trackTypeRepository.createNewTrackType(trackTypeName, selectedValueType, min, max,
            object : TrackTypeRepository.CreateTrackTypeCallback {
                override fun onTrackTypeCreated() {
                    trackTypeView?.showTrackTypeCreatedMsg(trackTypeName)
                }

                override fun onError() {
                    trackTypeView?.showCannotCreateTrackTypeMsg()
                }
            })
    }
}