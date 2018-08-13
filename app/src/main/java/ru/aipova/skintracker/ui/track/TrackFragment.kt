package ru.aipova.skintracker.ui.track

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.track_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.utils.PhotoUtils


class TrackFragment : Fragment(), TrackContract.View {

    override lateinit var presenter: TrackContract.Presenter
    private var trackValueDataArray:Array<TrackValueData> = arrayOf()
    private var seekBars: MutableList<SeekBar> = mutableListOf()

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
        setupPhotoButton()
        loadPhoto()
    }

    private fun setupPhotoButton() {
        takePhotoBtn.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePhotoIntent.resolveActivity(activity!!.packageManager) != null) {
                val photoFile = PhotoUtils.constructPhotoFile(presenter.getPhotoFileName(), activity!!)
                val photoUri = FileProvider.getUriForFile(
                    activity!!,
                    "ru.aipova.skintracker.fileprovider",
                    photoFile
                )
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(takePhotoIntent, REQUEST_PHOTO)
            }
        }
    }

    override fun initTrackValues(trackValueData: Array<TrackValueData>) {
        trackValueDataArray = trackValueData
        for ((index, trackData) in trackValueDataArray.withIndex()) {
            val textView = TextView(activity).apply { text = trackData.name }
            trackValuesLayout.addView(textView)

            val seekBar = SeekBar(activity).apply {
                id = index
                max = SEEK_BAR_MAX
                progress = trackData.value
            }
            trackValuesLayout.addView(seekBar)
            seekBars.add(seekBar)
        }
    }

    override fun setupNoteText(text: String) {
        noteTxt.setText(text)
    }

    private fun restoreSeekBarsProgress(savedInstanceState: Bundle?) {
        val savedSeekBarValues =
            if (savedInstanceState != null) savedInstanceState.getSerializable(SEEK_BAR_VALUES) as? IntArray else null
        savedSeekBarValues?.forEachIndexed { index, value ->
            seekBars[index].progress = value
        }
    }

    private fun loadPhoto() {
        val file = PhotoUtils.constructPhotoFile(presenter.getPhotoFileName(), activity!!)
        Picasso.get().invalidate(file)
        Picasso.get().load(file).into(photoView)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(SEEK_BAR_VALUES, getTrackValues())
    }

    private fun getTrackValues() = seekBars.map { it.progress }.toIntArray()

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_track, menu)
        (activity as AppCompatActivity).supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_close)
            title = getString(R.string.title_new_track)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            loadPhoto()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save_track -> {
            saveData()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun saveData() {
        val trackValues = getTrackValues()
        trackValueDataArray.forEachIndexed { index, trackValueData ->
            trackValueData.value = trackValues[index]
        }
        presenter.saveTrackData(trackValueDataArray, noteTxt.text.toString())
    }

    override fun showTrackCreatedMsg() {
        Toast.makeText(activity, "Track Created", Toast.LENGTH_LONG).show()
        activity?.setResult(RESULT_OK)
        activity?.finish()
    }

    override fun showCannotCreateTrackMsg() {
        Toast.makeText(activity, "Cannot create track", Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val SEEK_BAR_VALUES = "seek_bar"
        private const val SEEK_BAR_MAX = 10
        private const val REQUEST_PHOTO = 1
        fun newInstance(): TrackFragment {
            return TrackFragment()
        }
    }
}
