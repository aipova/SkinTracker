package ru.aipova.skintracker.ui.view

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.track.TrackValueData

class TrackValuesView : LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        orientation = LinearLayout.VERTICAL
    }

    private var seekBars: MutableList<SeekBar> = mutableListOf()

    fun setTrackValues(trackValues: Array<TrackValueData>, editable: Boolean) {
        post {
            trackValues.forEachIndexed { index, trackValueData ->
                addView(trackValueView(index, trackValueData, editable))
            }
        }
    }

    private fun trackValueView(index: Int, trackValue: TrackValueData, editable: Boolean): View {
        val view = LayoutInflater.from(context).inflate(R.layout.track_value_item, this, false)
        view.findViewById<TextView>(R.id.nameTxt).apply { text = trackValue.name }
        val seekBar = view.findViewById<SeekBar>(R.id.valueSeekBar).apply {
            id = index
            max = SEEK_BAR_MAX
            progress = trackValue.value
            isEnabled = editable
        }
        seekBars.add(seekBar)
        return view
    }

    fun updateValues(values: IntArray) {
        post {
            values.forEachIndexed { index, value ->
                seekBars[index].progress = value
            }
        }
    }

    fun getTrackValues() = seekBars.map { it.progress }.toIntArray()

    companion object {
        const val SEEK_BAR_MAX = 10
    }




}