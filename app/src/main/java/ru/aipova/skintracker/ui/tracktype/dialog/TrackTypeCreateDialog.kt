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
        fun checkNewTrackTypeName(trackTypeName: String): Boolean
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
        return parentFragment.let {
            if (it is Callbacks) {
                it.checkNewTrackTypeName(trackTypeName)
            } else {
                false
            }
        }
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