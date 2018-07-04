package ru.aipova.skintracker.ui.tracktype

import ru.aipova.skintracker.R

class TrackTypeCreateDialog : TrackTypeDialog() {

    interface Callbacks {
        fun onCreateNewTrackType(trackTypeName: String)
    }

    override fun onOkButtonClick(trackTypeName: String) {
        parentFragment.let {
            if (it is TrackTypeCreateDialog.Callbacks) {
                it.onCreateNewTrackType(trackTypeName)
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