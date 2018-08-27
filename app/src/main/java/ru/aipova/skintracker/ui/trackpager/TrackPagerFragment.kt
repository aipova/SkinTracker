package ru.aipova.skintracker.ui.trackpager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import com.squareup.picasso.Picasso
import io.realm.RealmList
import kotlinx.android.synthetic.main.track_pager_fragment.*
import ru.aipova.skintracker.InjectionStub.trackRepository
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.TrackValue
import ru.aipova.skintracker.utils.PhotoUtils
import java.util.*

class TrackPagerFragment : Fragment() {
    private lateinit var tracks: RealmList<TrackValue>
    private var track: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        track = trackRepository.getTrackByDate(getTrackDate())
        tracks = track?.values ?: RealmList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId =
            if (track == null) R.layout.track_pager_empty_fragment else R.layout.track_pager_fragment
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        track?.note?.let {
            if (it.isNotBlank()) {
                noteTxt.text = it
                noteTxt.visibility = View.VISIBLE
                photoCard.visibility = View.VISIBLE
            }
        }

        track?.date?.let { trackDate ->
            loadPhoto(trackDate)
        }

        for (track in tracks) {
            val textView = TextView(activity).apply { text = track.trackType?.name }
            trackValuesLayout.addView(textView)
            val seekBar = SeekBar(activity).apply {
                max = SEEK_BAR_MAX
                progress = track.value?.toInt() ?: 0
                isEnabled = false
            }
            trackValuesLayout.addView(seekBar)
        }
    }

    private fun loadPhoto(trackDate: Date) {
        val file = PhotoUtils.constructPhotoFile(trackDate, activity!!)
        if (file.exists()) {
            Picasso.get().invalidate(file)
            Picasso.get().load(file).into(trackPhoto)
            trackPhoto.visibility = View.VISIBLE
            photoCard.visibility = View.VISIBLE
        } else {
            trackPhoto.visibility = View.GONE
        }
    }

    private fun getTrackDate() = arguments?.getSerializable(DATE) as Date

    companion object {
        private const val DATE = "date"
        private const val SEEK_BAR_MAX = 10

        fun getInstance(date: Date): TrackPagerFragment {
            val bundle = Bundle().apply { putSerializable(DATE, date) }
            return TrackPagerFragment().apply { arguments = bundle }
        }
    }
}