package ru.aipova.skintracker.ui.trackpager.track

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.track_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.ui.data.TrackValueData
import java.io.File
import java.util.*
import javax.inject.Inject

class TrackFragment : DaggerFragment(), Observer, TrackContract.View {

    @Inject
    override lateinit var presenter: TrackContract.Presenter

    interface Callbacks {
        fun onViewTouchDown()
        fun onViewTouchUp()
    }

    private var callbacks: Callbacks? = null
    override var isActive: Boolean = false
        get() = isAdded

    override fun setupTrackExists(isExisting: Boolean) {
        trackExists = isExisting
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Callbacks) {
            callbacks = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private var trackExists = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        presenter.takeView(this)
        presenter.init(getTrackDate())
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.dropView()
    }

    private fun getLayoutId() =
        if (trackExists) R.layout.track_fragment else R.layout.track_pager_empty_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter.start()
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    callbacks?.onViewTouchDown()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    callbacks?.onViewTouchUp()
                    true
                }
                else -> false
            }
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        if (isActive && arg is TrackData && trackExists) {
            presenter.onUpdateNote(arg)
        }
    }

    override fun setupNoteText(text: String) {
        noteTxt.text = text
    }

    override fun showTrackValues(trackValueData: Array<TrackValueData>) {
        valuesCard.visibility = View.VISIBLE
        trackValuesView.setTrackValues(trackValueData, false)
    }

    override fun loadPhoto(photoFile: File) {
        Picasso.get().load(photoFile).fit().centerCrop().into(trackPhoto)
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

        fun getInstance(date: Date): TrackFragment {
            val bundle = Bundle().apply { putSerializable(DATE, date) }
            return TrackFragment()
                .apply { arguments = bundle }
        }
    }
}