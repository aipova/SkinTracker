package ru.aipova.skintracker.ui.track

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.TrackFields
import ru.aipova.skintracker.model.TrackType
import java.util.*

class TrackFragment : Fragment() {

    private lateinit var trackTypes: RealmResults<TrackType>
    private lateinit var realm: Realm
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        realm = Realm.getDefaultInstance()
        trackTypes = realm.where<TrackType>().findAll()
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
        layout = view.findViewById(R.id.track_layout)
        val savedSeekBarValues =
            if (savedInstanceState != null) savedInstanceState.getSerializable(SEEK_BAR_VALUES) as HashMap<Int, Int>
            else hashMapOf()

        for ((index, trackType) in trackTypes.withIndex()) {
            val tv = TextView(activity)
            tv.text = trackType.name
            layout.addView(tv)

            val sb = SeekBar(activity).apply {
                id = index
                max = SEEK_BAR_MAX
                savedSeekBarValues[index]?.let {
                    progress = it
                }
            }
            layout.addView(sb)
        }
        return view
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
            title = getString(R.string.track_title)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save_track -> {
            saveData()
            Toast.makeText(activity, "Track Created", Toast.LENGTH_LONG).show()
            activity?.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun saveData() {
        for (i in 0..layout.childCount) {
            val child = layout.getChildAt(i)
            val todayTracks = mutableListOf<Track>()
            val existingTracks = realm.where<Track>().equalTo(TrackFields.DATE, today()).findAll()
            realm.executeTransaction { realm ->
                if (child is SeekBar) {
                    val track = Track().apply {
                        trackType = trackTypes[child.id]
                        value = child.progress.toLong()
                        date = today()
                    }
                    val existingTrack = existingTracks.find { it.trackType == track.trackType }
                    existingTrack?.let {
                        it.value = track.value
                    } ?: todayTracks.add(track)
                }
                realm.insertOrUpdate(todayTracks)
            }
        }
    }

    private fun today(): Date? {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    companion object {
        const val SEEK_BAR_VALUES = "seek_bar"
        const val SEEK_BAR_MAX = 10
        fun newInstance(): TrackFragment {
            return TrackFragment()
        }
    }
}
