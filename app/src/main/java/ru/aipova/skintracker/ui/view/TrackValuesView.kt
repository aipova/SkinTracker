package ru.aipova.skintracker.ui.view

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v7.widget.SwitchCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Checkable
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import com.travijuu.numberpicker.library.NumberPicker
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.ValueType
import ru.aipova.skintracker.ui.data.TrackValueData

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

    private var views: MutableList<View> = mutableListOf()

    fun setTrackValues(trackValues: Array<TrackValueData>, editable: Boolean) {
        post {
            trackValues.forEachIndexed { index, trackValueData ->
                addView(trackValueView(index, trackValueData, editable))
            }
        }
    }

    private fun trackValueView(index: Int, trackValue: TrackValueData, editable: Boolean): View {
        val view = LayoutInflater.from(context).inflate(R.layout.track_value_item, this, false)
        val trackNameTxt = view.findViewById<TextView>(R.id.nameTxt).apply { text = trackValue.name }
        when(trackValue.type) {
            ValueType.SEEK -> {
                if (editable) {
                    val seekBar = createSeekBar(view, index, trackValue, trackNameTxt)
                    views.add(seekBar)
                }
                trackNameTxt.text = seekText(trackValue)
            }
            ValueType.AMOUNT -> {
                if (editable) {
                    val amountView = createAmountView(view, index, trackValue)
                    views.add(amountView)
                } else {
                    trackNameTxt.text = amountText(trackValue)
                }
            }
            ValueType.BOOLEAN -> {
                if (editable) {
                    val switch = createSwitchView(view, index, trackValue, editable)
                    views.add(switch)
                } else {
                    trackNameTxt.text = booleanText(trackValue)
                }

            }
        }

        return view
    }

    private fun createSwitchView(
        view: View,
        index: Int,
        trackValue: TrackValueData,
        editable: Boolean
    ): SwitchCompat {
        return view.findViewById<SwitchCompat>(R.id.valueSwitch).apply {
            id = index
            isChecked = trackValue.value != 0
            isEnabled = editable
            visibility = View.VISIBLE
        }
    }

    private fun createAmountView(
        view: View,
        index: Int,
        trackValue: TrackValueData
    ): NumberPicker {
        return view.findViewById<NumberPicker>(R.id.valueNum).apply {
            id = index
            value = trackValue.value
            visibility = View.VISIBLE
        }
    }

    private fun createSeekBar(
        view: View,
        index: Int,
        trackValue: TrackValueData,
        trackNameTxt: TextView
    ): SeekBar {
        return view.findViewById<SeekBar>(R.id.valueSeekBar).apply {
            id = index
            max = trackValue.max
            progress = trackValue.value
            visibility = View.VISIBLE
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                val trackName = trackValue.name
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    trackNameTxt.text = seekText(trackName, progress, max)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })
        }
    }

    private fun amountText(trackValue: TrackValueData) =
        context.getString(R.string.track_value_amount, trackValue.name, trackValue.value.toString())

    private fun seekText(trackValue: TrackValueData) =
        seekText(trackValue.name, trackValue.value, trackValue.max)

    private fun seekText(trackName: String, trackValue: Int, maxValue: Int) =
        context.getString(
            R.string.track_value_seek,
            trackName,
            trackValue.toString(),
            maxValue.toString()
        )

    private fun booleanText(trackValue: TrackValueData) =
        context.getString(
            if (trackValue.value == 0) R.string.track_value_boolean_false else R.string.track_value_boolean_true,
            trackValue.name
        )


    fun updateValues(values: IntArray) {
        post {
            values.forEachIndexed { index, value ->
                val view = views[index]
                when(view) {
                    is SeekBar -> { view.progress = value }
                    is NumberPicker -> { view.value = value }
                    is Checkable -> { view.isChecked = value != 0 }
                }
            }
        }
    }

    fun getTrackValues() = views.map {
        when(it) {
            is SeekBar -> it.progress
            is NumberPicker -> it.value
            is Checkable -> if (it.isChecked) 1 else 0
            else -> 0
        }
    }.toIntArray()
}