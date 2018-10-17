package ru.aipova.skintracker.ui.statistics

import android.app.DatePickerDialog
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import java.util.*

interface StatisticsContract {
    interface View: BaseView<Presenter> {
        val isActive: Boolean
        fun drawLegend(
            legendColors: Map<String, Int>,
            onLegendChoose: () -> Unit
        )
        fun setupChartProperties(axisValueFormatter: IAxisValueFormatter)
        fun setupDateRangeSelector()
        fun showDateRangeText()
        fun setDateRangeText(startDate: Date, endDate: Date)
        fun showDateRangeButtons()
        fun showDatePickerDialog(
            date: Date,
            dateChangedListener: DatePickerDialog.OnDateSetListener
        )
        fun loadChartForSelectedRange()
        fun getSelectedLegend(): List<String>
        fun clearChart()
        fun showAtChart(lineData: LineData)
    }

    interface Presenter: BasePresenter {
        fun weekPeriodSelected()
        fun monthPeriodSelected()
        fun customPeriodSelected()
        fun chooseStartDate()
        fun chooseEndDate()
        fun stop()
        fun getStartDate(): Date
        fun getEndDate(): Date
        fun updateDates(start: Date, end: Date)
        fun takeView(view: View)
        fun dropView()
    }
}