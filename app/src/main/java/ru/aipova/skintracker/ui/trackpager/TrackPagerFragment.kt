package ru.aipova.skintracker.ui.trackpager

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.track_pager_fragment.*
import ru.aipova.skintracker.InjectionStub
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.track.TrackValueData
import java.io.File
import java.util.*

class TrackPagerFragment : Fragment(), TrackPagerContract.View {
    override var isActive: Boolean = false
        get() = isAdded

    override fun setupTrackExists(isExisting: Boolean) {
        trackExists = isExisting
    }

    override lateinit var presenter: TrackPagerContract.Presenter
    private var trackExists = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = TrackPagerPresenter(
            this,
            getTrackDate(),
            InjectionStub.trackRepository,
            InjectionStub.photoUtils
        )
        presenter.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    private fun getLayoutId() =
        if (trackExists) R.layout.track_pager_fragment else R.layout.track_pager_empty_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.start()
    }

    override fun setupNoteText(text: String) {
        noteTxt.text = text
    }

    override fun showTrackValues(trackValueData: Array<TrackValueData>) {
        trackValuesView.setTrackValues(trackValueData, false)
    }

    override fun loadPhoto(photoFile: File) {
        Picasso.get().invalidate(photoFile)
        Picasso.get().load(photoFile).into(trackPhoto)
    }

    override fun showPhotoView() {
        photoCard.visibility = View.VISIBLE
        trackPhoto.visibility = View.VISIBLE
    }

    override fun showNoteView() {
        photoCard.visibility = View.VISIBLE
        noteTxt.visibility = View.VISIBLE
    }

    override fun hidePhotoView() {
        trackPhoto.visibility = View.GONE
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