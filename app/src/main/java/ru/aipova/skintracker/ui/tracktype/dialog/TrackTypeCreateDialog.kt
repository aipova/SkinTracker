package ru.aipova.skintracker.ui.tracktype.dialog

import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.ValueType

class TrackTypeCreateDialog : TrackTypeDialog() {

    interface Callbacks {
        fun onCreateNewTrackType(
            trackTypeName: String,
            selectedValueType: ValueType,
            min: Int,
            max: Int
        )
    }

    override fun onOkButtonClick(
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    ) {
        parentFragment.let {
            if (it is Callbacks) {
                it.onCreateNewTrackType(trackTypeName, selectedValueType, min, max)
            }
        }
    }

    override fun nameIsDuplicate(trackTypeName: String): Boolean {
        val existingTrack = trackTypeRepository.findByName(trackTypeName)
        return existingTrack != null
    }

    override fun getTitle(): Int {
        return R.string.title_new_track_type

    }

    companion object {
        fun newInstance(): TrackTypeCreateDialog {
            return TrackTypeCreateDialog()
        }
    }
}