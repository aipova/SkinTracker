package ru.aipova.skintracker.ui.statistics

import android.app.DatePickerDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.statistics_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.*
import ru.aipova.skintracker.utils.TimeUtils
import java.util.*

class StatisticsFragment : Fragment() {

    private lateinit var realm: Realm
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var trackTypes: RealmResults<TrackType>
    private val xAxisValues = mutableListOf<String>()
    private val colors = ColorTemplate.COLORFUL_COLORS
        .plus(ColorTemplate.JOYFUL_COLORS)
        .plus(ColorTemplate.VORDIPLOM_COLORS)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        realm = Realm.getDefaultInstance()
        return inflater.inflate(R.layout.statistics_fragment, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        realm.close()
    }

    private var dataset = mutableMapOf<String, ILineDataSet>()
    private var legendColors = mutableMapOf<TrackType, Int>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // TODO async
        trackTypes = realm.where<TrackType>()
            .`in`(TrackTypeFields.VALUE_TYPE, arrayOf(ValueType.SEEK.name, ValueType.AMOUNT.name))
            .findAll()
        setupChartLegend()
        setupChartProperties()
        setupDateRange()
    }

    private fun setupDateRange() {
        setupDateRangeSelection()
        dateFromBtn.setOnClickListener { showStartDatePickerDialog() }
        dateToBtn.setOnClickListener { showEndDatePickerDialog() }
        dateRangeSpinner.setSelection(0)
    }

    private fun setupChartLegend() {
        setupLegendColors()
        drawLegend()
    }

    private fun drawLegend() {
        legendColors.forEach { (trackType, color) ->
            val checkBox = CheckBox(activity)
            checkBox.text = trackType.name
            checkBox.isChecked = true
            checkBox.setTextColor(color)
            checkBox.setOnCheckedChangeListener { buttonView, isChecked -> redrawChart() }
            legendLayout.addView(checkBox)
        }
    }

    private fun setupLegendColors() {
        var colorIndex = 0
        trackTypes.forEach {
            val color = if (colorIndex < colors.size) colorIndex else colorIndex.rem(colors.size)
            legendColors[it] = colors[color]
            colorIndex++
        }
    }

    private fun setupDateRangeSelection() {
        val spinnerAdapter = ArrayAdapter<String>(
            activity,
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.date_range_array)
        )
        dateRangeSpinner.adapter = spinnerAdapter
        dateRangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        showDateRangeText()
                        startDate = TimeUtils.weekAgoDate()
                        endDate = TimeUtils.todayDate()
                        resetChart()
                    }
                    1 -> {
                        showDateRangeText()
                        startDate = TimeUtils.monthAgoDate()
                        endDate = TimeUtils.todayDate()
                        resetChart()
                    }
                    2 -> {
                        showDateRangeButtons()
                    }
                }

            }
        }
    }

    private fun showStartDatePickerDialog() {
        showDatePickerDialog(startDate, startDateChangedListener)
    }

    private fun showEndDatePickerDialog() {
        showDatePickerDialog(endDate, endDateChangedListener)
    }

    private fun showDatePickerDialog(
        date: Date,
        dateChangedListener: DatePickerDialog.OnDateSetListener
    ) {
        val selectedDate = Calendar.getInstance().apply { time = date }
        DatePickerDialog(
            activity,
            dateChangedListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private val startDateChangedListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            startDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
            resetChart()
        }

    private val endDateChangedListener =
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            endDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time
            resetChart()
        }

    fun resetChart() {
        setDateRangeText(startDate, endDate)
        clearOldData()
        loadData()
        redrawChart()
    }

    private fun clearOldData() {
        dataset.clear()
        xAxisValues.clear()
    }

    private fun loadData() {
        val tracksMap = fillValuesMap(startDate, endDate)
        fillDataset(tracksMap)
    }

    private fun redrawChart() {
        val selectedDataset = (0 until legendLayout.childCount)
            .map { index -> legendLayout.getChildAt(index) as CheckBox}
            .filter { it.isChecked }
            .map { it.text }
            .mapNotNull { dataset[it] }
        if (selectedDataset.isNotEmpty()) {
            chart.data = LineData(selectedDataset)
            chart.invalidate()
        } else {
            chart.clear()
        }


    }

    private fun fillDataset(tracksMap: MutableMap<TrackType, MutableList<Entry>>) {
        tracksMap.forEach { (trackType, values) ->
            val line = LineDataSet(values, trackType.name)
            line.color = legendColors[trackType]!!
            line.circleColors = listOf(line.color)
            // TODO validate unique name on creation
            dataset[trackType.name] = line
        }
    }

    private fun fillValuesMap(
        startDate: Date,
        endDate: Date
    ): MutableMap<TrackType, MutableList<Entry>> {
        val tracksMap = mutableMapOf<TrackType, MutableList<Entry>>()
        val tracks = realm.where<Track>().between(TrackFields.DATE, startDate, endDate)
            .sort(TrackFields.DATE).findAll()
        tracks.forEachIndexed { index, track ->
            xAxisValues.add(TimeUtils.getDateFormatted(track.date))
            track.values.forEach {
                if (trackTypes.contains(it.trackType)) {
                    val trackValues = tracksMap[it.trackType] ?: mutableListOf()
                    trackValues.add(Entry(index.toFloat(), it.value?.toFloat() ?: 0f))
                    tracksMap[it.trackType!!] = trackValues
                }
            }
        }
        return tracksMap
    }

    private fun setDateRangeText(startDate: Date, endDate: Date) {
        val formattedStart = TimeUtils.getDateFormatted(startDate)
        val formattedEnd = TimeUtils.getDateFormatted(endDate)
        dateRangeTxt.text = getString(
            R.string.date_range,
            formattedStart,
            formattedEnd
        )
        dateFromBtn.text = formattedStart
        dateToBtn.text = formattedEnd
    }

    fun showDateRangeText() {
        dateRangeTxt.visibility = View.VISIBLE
        dateBtnGroup.visibility = View.GONE
    }

    fun showDateRangeButtons() {
        dateRangeTxt.visibility = View.GONE
        dateBtnGroup.visibility = View.VISIBLE
    }

    private fun setupChartProperties() {
        with(chart) {
            description.isEnabled = false
            setDrawBorders(true)
            setTouchEnabled(false)
            legend.isEnabled = false
            setNoDataText(getString(R.string.msg_no_chart_data))

            axisLeft.setDrawGridLines(false)
            axisRight.setDrawGridLines(false)
            with(xAxis) {
                spaceMax = 0.05f
                spaceMin = 0.05f
                position = XAxis.XAxisPosition.BOTTOM
                setDrawAxisLine(true)
                setDrawGridLines(false)
                setDrawLabels(true)
                granularity = 1f
                setValueFormatter { value, axis ->
                    val index = value.toInt()
                    if (index >= xAxisValues.size || index < 0) {
                        ""
                    } else {
                        xAxisValues[value.toInt()]
                    }
                }

            }
        }
    }


    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
