package ru.aipova.skintracker.ui.tracktype.dialog

import android.os.Bundle
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.ValueType

class TrackTypeEditDialog : TrackTypeDialog() {

    interface Callbacks {
        fun onEditTrackType(
            trackType: TrackType,
            trackTypeName: String,
            selectedValueType: ValueType,
            min: Int,
            max: Int
        )

        fun checkEditTrackTypeName(trackType: TrackType, trackTypeName: String): Boolean
    }

    override fun onOkButtonClick(
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    ) {
        parentFragment.let {
            if (it is Callbacks) {
                it.onEditTrackType(getTrackType(), trackTypeName, selectedValueType, min, max)
            }
        }
    }

    override fun nameIsDuplicate(trackTypeName: String): Boolean {
        return parentFragment.let {
            if (it is Callbacks) {
                it.checkEditTrackTypeName(getTrackType(), trackTypeName)
            } else {
                return false
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

    override fun getValueType(): ValueType {
        return getTrackType().getValueTypeEnum()
    }

    override fun getMinValue(): Int {
        return getTrackType().minValue.toInt()
    }

    override fun getMaxValue(): Int {
        return getTrackType().maxValue.toInt()
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