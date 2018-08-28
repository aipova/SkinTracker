package ru.aipova.skintracker.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.track.TrackValueData

class TrackValuesView(context: Context?) : LinearLayout(context) {
    private var seekBars: MutableList<SeekBar> = mutableListOf()

    init {
        orientation = LinearLayout.VERTICAL
    }

    fun setPadding() {
        context?.let {
            val padding = it.resources.getDimensionPixelSize(R.dimen.default_margin)
            setPadding(padding, padding, padding, padding)
        }
    }

    fun setTrackValues(trackValues: Array<TrackValueData>, editable: Boolean) {
        trackValues.forEachIndexed { index, trackValueData ->
            addView(trackValueView(index, trackValueData, editable))
        }
    }

    private fun trackValueView(index: Int, trackValue: TrackValueData, editable: Boolean): View {
        val view = LayoutInflater.from(context).inflate(R.layout.track_value_item, this, false)
        view.findViewById<TextView>(R.id.nameTxt).apply { text = trackValue.name }
        val seekBar = view.findViewById<SeekBar>(R.id.valueSeekBar).apply {
            id = index
            max = SEEK_BAR_MAX
            progress = trackValue.value
        }
        seekBars.add(seekBar)
        return view
    }

    fun updateValues(values: IntArray) {
        values.forEachIndexed { index, value ->
            seekBars[index].progress = value
        }
    }

    fun getTrackValues() = seekBars.map { it.progress }.toIntArray()

    companion object {
        const val SEEK_BAR_MAX = 10
    }




}