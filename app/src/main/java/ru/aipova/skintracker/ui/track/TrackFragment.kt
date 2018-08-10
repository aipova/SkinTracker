package ru.aipova.skintracker.ui.track

import android.app.Activity.RESULT_OK
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import io.realm.OrderedRealmCollectionSnapshot
import io.realm.Realm
import io.realm.RealmList
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.track_fragment.*
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackValue
import java.util.*

class TrackFragment : Fragment() {

    private lateinit var trackTypes: OrderedRealmCollectionSnapshot<TrackType>
    private lateinit var realm: Realm
    private lateinit var layout: LinearLayout
    private lateinit var currentDate: Date
    private var existingTrack:Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentDate = arguments?.getSerializable(DATE_PARAMETER) as? Date ?: Date()
        truncateToDay(currentDate)
        setHasOptionsMenu(true)
        retainInstance = true
        realm = Realm.getDefaultInstance()
        trackTypes = realm.where<TrackType>().findAll().createSnapshot()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.track_fragment, container, false)
        layout = view.findViewById(R.id.trackValuesLayout)
        val savedSeekBarValues =
            if (savedInstanceState != null) savedInstanceState.getSerializable(SEEK_BAR_VALUES) as HashMap<Int, Int>
            else hashMapOf()

        existingTrack = getExistingTrack()
        val existingTracks = existingTrack?.values ?: RealmList()
        for ((index, trackType) in trackTypes.withIndex()) {
            val tv = TextView(activity)
            tv.text = trackType.name
            layout.addView(tv)

            val sb = SeekBar(activity).apply {
                id = index
                max = SEEK_BAR_MAX
                val existingTrackValue = existingTracks.find { it.trackType == trackType }
                existingTrackValue?.let {
                    progress = it.value?.toInt() ?: 0
                }
                savedSeekBarValues[index]?.let {
                    progress = it
                }
            }
            layout.addView(sb)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        noteTxt.setText(existingTrack?.note)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val states = hashMapOf<Int, Int>()
        for (i in 0..layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is SeekBar) {
                states[child.id] = child.progress
            }
        }
        outState.putSerializable(SEEK_BAR_VALUES, states)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_track, menu)
        (activity as AppCompatActivity).supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
            title = getString(R.string.title_new_track)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save_track -> {
            saveData()
            Toast.makeText(activity, "Track Created", Toast.LENGTH_LONG).show()
            activity?.setResult(RESULT_OK)
            activity?.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun saveData() {
        for (i in 0..layout.childCount) {
            val child = layout.getChildAt(i)
            val todayTracks = mutableListOf<TrackValue>()

            realm.executeTransaction { realm ->
                val currentTrack = getOrCreateCurrentTrack()
                val existingTrackValues = currentTrack.values

                if (child is SeekBar) {
                    val trackValue = TrackValue().apply {
                        trackType = trackTypes[child.id]
                        value = child.progress.toLong()
                    }
                    val existingTrackValue = existingTrackValues.find { it.trackType == trackValue.trackType }
                    existingTrackValue?.let {
                        it.value = trackValue.value
                    } ?: todayTracks.add(trackValue)
                }
                realm.insertOrUpdate(todayTracks)
                currentTrack.values.addAll(todayTracks)
                currentTrack.note = noteTxt.text.toString()
                realm.insertOrUpdate(currentTrack)
            }
        }
    }

    private fun getOrCreateCurrentTrack(): Track {
        return existingTrack ?: Track().apply {
            date = currentDate

        }
    }

    private fun getExistingTrack() = InjectionStub.trackRepository.getTrackByDate(currentDate)


    private fun truncateToDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    companion object {
        private const val SEEK_BAR_VALUES = "seek_bar"
        private const val DATE_PARAMETER = "date"
        private const val SEEK_BAR_MAX = 10
        fun newInstance(date: Date): TrackFragment {
            val bundle = Bundle().apply { putSerializable(DATE_PARAMETER, date) }
            return TrackFragment().apply { arguments = bundle }
        }
    }
}
