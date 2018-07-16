package ru.aipova.skintracker.ui.trackpager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.RealmResults
import kotlinx.android.synthetic.main.track_pager_fragment.*
import ru.aipova.skintracker.InjectionStub.trackRepository
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.Track
import java.util.*

class TrackPagerFragment : Fragment() {
    private lateinit var tracks: RealmResults<Track>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tracks = trackRepository.getTracksByDate(getTrackDate())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.track_pager_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dateTxt.text = getTrackDate().toString()
        for (track in tracks) {
            val tv = TextView(activity)
            tv.text = "${track.trackType?.name} - ${track.value}"
            layout.addView(tv)
        }
    }

    private fun getTrackDate() = arguments?.getSerializable(DATE) as Date

    companion object {
        private const val DATE = "date"

        fun getInstance(date: Date): TrackPagerFragment {
            val bundle = Bundle().apply { putSerializable(DATE, date) }
            return TrackPagerFragment().apply { arguments = bundle }
        }
    }
}