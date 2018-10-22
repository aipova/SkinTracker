package ru.aipova.skintracker.ui.statistics

import android.app.DatePickerDialog
import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackTypeRepository
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.utils.TimeUtils
import java.util.*
import javax.inject.Inject

class StatisticsPresenter
    @Inject constructor(
    private val trackRepository: TrackRepository,
    private val trackTypeRepository: TrackTypeRepository
) : StatisticsContract.Presenter {
    private var statisticsView: StatisticsContract.View? = null

    override fun takeView(view: StatisticsContract.View) {
        statisticsView = view
    }

    override fun dropView() {
        statisticsView = null
    }

    private lateinit var startDate: Date
    private lateinit var endDate: Date

    private var xAxisValues = listOf<String>()
    private var dataset = mapOf<String, ILineDataSet>()
    private var legend = mutableMapOf<String, Int>()

    private var dataDisposable: Disposable? = null
    private var legendDisposable: Disposable? = null

    override fun start() {
        statisticsView?.setupChartProperties(axisValueFormatter)
        statisticsView?.setupDateRangeSelector()
        setupChart()
    }

    private fun setupChart() {
        legendDisposable = Observable.fromCallable { trackTypeRepository.getChartTrackTypeNames() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setupAndDrawLegend(it)
                loadChartForLastWeek()
            }
    }

    private fun loadChartForLastWeek() {
        if (statisticsView != null) statisticsView?.loadChartForSelectedRange()
    }

    override fun chooseStartDate() {
        statisticsView?.showDatePickerDialog(startDate, startDateChangedListener)
    }

    override fun chooseEndDate() {
        statisticsView?.showDatePickerDialog(endDate, endDateChangedListener)
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

    private fun setupAndDrawLegend(trackTypes: List<String>) {
        setupLegend(trackTypes)
        if (statisticsView != null) statisticsView?.drawLegend(
            legend,
            onLegendChoose = { redrawChart() })
    }

    private fun setupLegend(trackTypes: List<String>) {
        trackTypes.forEachIndexed { index, trackType ->
            val color = if (index < colors.size) index else index.rem(colors.size)
            legend[trackType] = colors[color]
        }
    }

    override fun weekPeriodSelected() {
        statisticsView?.showDateRangeText()
        startDate = TimeUtils.weekAgoDate()
        endDate = TimeUtils.todayDate()
        resetChart()
    }

    override fun monthPeriodSelected() {
        statisticsView?.showDateRangeText()
        startDate = TimeUtils.monthAgoDate()
        endDate = TimeUtils.todayDate()
        resetChart()
    }

    override fun getStartDate(): Date {
        return startDate
    }

    override fun getEndDate(): Date {
        return endDate
    }

    override fun updateDates(start: Date, end: Date) {
        startDate = start
        endDate = end
    }

    override fun customPeriodSelected() {
        statisticsView?.showDateRangeButtons()
        resetChart()
    }

    private fun resetChart() {
        statisticsView?.setDateRangeText(startDate, endDate)
        loadData(onLoadComplete = { redrawChart() })
    }

    private fun redrawChart() {
        statisticsView?.let {
            val selectedDataset = it.getSelectedLegend().mapNotNull { dataset[it] }
            if (selectedDataset.isNotEmpty()) {
                it.showAtChart(LineData(selectedDataset))
            } else {
                it.clearChart()
            }
        }
    }

    private fun loadData(onLoadComplete: () -> Unit) {
        dataDisposable?.dispose()
        dataDisposable = Observable.fromCallable { trackRepository.findTracks(startDate, endDate) }
            .subscribeOn(Schedulers.io())
            .flatMap {
                xAxisValues = it.map { TimeUtils.getDateFormatted(it.date) }
                Observable.just(it)
            }
            .map { createValuesMap(it) }
            .map { createDataset(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                dataset = result
                onLoadComplete()
            }, { error ->
                Log.e("StatisticsPresenter", error.message, error)
            })
    }

    override fun stop() {
        dataDisposable?.dispose()
        legendDisposable?.dispose()
    }

    private fun createValuesMap(tracks: List<TrackData>): MutableMap<String, MutableList<Entry>> {
        val tracksMap = mutableMapOf<String, MutableList<Entry>>()
        tracks.forEachIndexed { index, track ->
            track.values.forEach {
                if (legend.contains(it.name)) {
                    val trackValues = tracksMap[it.name] ?: mutableListOf()
                    trackValues.add(Entry(index.toFloat(), it.value.toFloat()))
                    tracksMap[it.name] = trackValues
                }
            }
        }
        return tracksMap
    }

    private fun createDataset(tracksMap: MutableMap<String, MutableList<Entry>>): Map<String, ILineDataSet> {
        return tracksMap.mapValues { entry -> createLine(entry.key, entry.value) }
    }

    private fun createLine(
        trackType: String,
        values: MutableList<Entry>
    ): LineDataSet {
        return LineDataSet(values, trackType).apply {
            color = legend[trackType]!!
            circleColors = listOf(color)
        }
    }

    private val axisValueFormatter =
        IAxisValueFormatter { value, axis ->
            val index = value.toInt()
            if (index >= xAxisValues.size || index < 0) {
                ""
            } else {
                xAxisValues[value.toInt()]
            }
        }

    companion object {
        private val colors = ColorTemplate.COLORFUL_COLORS
            .plus(ColorTemplate.JOYFUL_COLORS)
            .plus(ColorTemplate.VORDIPLOM_COLORS)
    }
}