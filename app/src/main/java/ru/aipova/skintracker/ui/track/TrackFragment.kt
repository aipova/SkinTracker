package ru.aipova.skintracker.ui.track

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.track_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.utils.TimeUtils
import java.util.*


class TrackFragment : Fragment(), TrackContract.View {

    override lateinit var presenter: TrackContract.Presenter
    override var isActive: Boolean = false
        get() = isAdded

    private var trackValueDataArray:Array<TrackValueData> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.track_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.start()
        restoreSeekBarsProgress(savedInstanceState)
    }

    override fun showTrackValues(trackValueData: Array<TrackValueData>) {
        trackValueDataArray = trackValueData
        trackValuesView.setTrackValues(trackValueData, true)
    }

    private fun restoreSeekBarsProgress(savedInstanceState: Bundle?) {
        getSavedSeekBarValues(savedInstanceState)?.let {
            trackValuesView.updateValues(it)
        }
    }

    private fun getSavedSeekBarValues(savedInstanceState: Bundle?) =
        if (savedInstanceState != null) savedInstanceState.getSerializable(SEEK_BAR_VALUES) as? IntArray else null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SEEK_BAR_VALUES, trackValuesView.getTrackValues())
    }

    override fun setTitle(date: Date) {
        (activity as AppCompatActivity).supportActionBar?.run {
            title = getString(R.string.title_track_parameters)
            subtitle = TimeUtils.getDateFormatted(date)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_track, menu)
        (activity as AppCompatActivity).supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save_track -> {
            presenter.save()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun getTrackValueData(): Array<TrackValueData> {
        val trackValues = trackValuesView.getTrackValues()
        trackValueDataArray.forEachIndexed { index, trackValueData ->
            trackValueData.value = trackValues[index]
        }
        return trackValueDataArray
    }

    override fun showTrackCreatedMsg() {
        Toast.makeText(activity, R.string.msg_track_created, Toast.LENGTH_LONG).show()
    }

    override fun showTrackUpdatedMsg() {
        Toast.makeText(activity, R.string.msg_track_updated, Toast.LENGTH_LONG).show()
    }

    override fun close() {
        activity?.setResult(RESULT_OK)
        activity?.finish()
    }

    override fun showCannotCreateTrackMsg() {
        Toast.makeText(activity, R.string.msg_cannot_create_track, Toast.LENGTH_LONG).show()
    }

    override fun showCannotLoadTrackMsg() {
        Toast.makeText(activity, R.string.msg_cannot_load_track, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val SEEK_BAR_VALUES = "ru.aipova.skintracker.track.SEEK_BAR_VALUES"
        fun newInstance(): TrackFragment {
            return TrackFragment()
        }
    }
}
