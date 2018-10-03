package ru.aipova.skintracker.ui.statistics

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.statistics_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.utils.TimeUtils
import java.util.*

//@ActivityScoped
class StatisticsFragment : DaggerFragment(), StatisticsContract.View {

//    @Inject
    override lateinit var presenter: StatisticsContract.Presenter

    override var isActive: Boolean = false
        get() = isAdded

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.statistics_fragment, container, false)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    //    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        presenter.takeView(this)
//    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }

    override fun setupDateRangeSelector() {
        setupDateRangeSelection()
        dateFromBtn.setOnClickListener { presenter.chooseStartDate() }
        dateToBtn.setOnClickListener { presenter.chooseEndDate() }
    }

    override fun loadChartForLastWeek() {
        dateRangeSpinner.setSelection(0)
    }

    override fun drawLegend(
        legendColors: Map<String, Int>,
        onLegendChoose: () -> Unit
    ) {
        legendColors.forEach { (trackType, color) ->
            legendLayout.addView(createLegendCheckbox(trackType, color, onLegendChoose))
        }
    }

    private fun createLegendCheckbox(
        trackType: String,
        color: Int,
        onLegendChoose: () -> Unit
    ): CheckBox {
        return CheckBox(activity).apply {
            text = trackType
            isChecked = true
            setTextColor(color)
            setOnCheckedChangeListener { bv, isChecked -> onLegendChoose() }
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
                    0 -> presenter.weekPeriodSelected()
                    1 -> presenter.monthPeriodSelected()
                    2 -> presenter.customPeriodSelected()
                }

            }
        }
    }


    override fun showDatePickerDialog(
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

    override fun getSelectedLegend(): List<String> {
        return (0 until legendLayout.childCount)
            .map { index -> legendLayout.getChildAt(index) as CheckBox}
            .filter { it.isChecked }
            .map { it.text.toString() }
    }

    override fun showAtChart(lineData: LineData) {
        chart.data = lineData
        chart.invalidate()
    }

    override fun clearChart() {
        chart.clear()
    }

    override fun setDateRangeText(startDate: Date, endDate: Date) {
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

    override fun showDateRangeText() {
        dateRangeTxt.visibility = View.VISIBLE
        dateBtnGroup.visibility = View.GONE
    }

    override fun showDateRangeButtons() {
        dateRangeTxt.visibility = View.GONE
        dateBtnGroup.visibility = View.VISIBLE
    }

    override fun setupChartProperties(axisValueFormatter: IAxisValueFormatter) {
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
                valueFormatter = axisValueFormatter
            }
        }
    }


    companion object {
        fun newInstance(): StatisticsFragment {
            return StatisticsFragment()
        }
    }
}
