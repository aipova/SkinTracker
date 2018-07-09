package ru.aipova.skintracker.ui.tracktype.dialog

import android.os.Bundle
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType

class TrackTypeEditDialog : TrackTypeDialog() {

    interface Callbacks {
        fun onEditTrackType(trackType: TrackType, trackTypeName: String)
    }

    override fun onOkButtonClick(trackTypeName: String) {
        parentFragment.let {
            if (it is Callbacks) {
                it.onEditTrackType(getTrackType(), trackTypeName)
            }
        }
    }

    private fun getTrackType() = arguments?.getSerializable(TRACK_TYPE) as TrackType

    override fun getTitle(): Int {
        return R.string.title_edit_track_type

    }

    override fun getNameText(): String? {
        return getTrackType().name
    }

    companion object {
        private const val TRACK_TYPE = "trackType"
        fun newInstance(trackType: TrackType): TrackTypeEditDialog {
            val bundle = Bundle().apply { putSerializable(TRACK_TYPE, trackType)}
            return TrackTypeEditDialog()
                .apply { arguments = bundle }
        }
    }


}