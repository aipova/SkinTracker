package ru.aipova.skintracker.ui.trackpager

import com.squareup.picasso.Picasso
import org.joda.time.LocalDate
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.utils.PhotoFileConstructor
import ru.aipova.skintracker.utils.TimeUtils
import java.io.File
import java.io.InputStream
import java.util.*
import javax.inject.Inject

class TrackPagerPresenter @Inject constructor(
    private val trackRepository: TrackRepository,
    private val photoFileConstructor: PhotoFileConstructor
) : TrackPagerContract.Presenter {
    private var trackPagerView: TrackPagerContract.View? = null

    override fun takeView(view: TrackPagerContract.View) {
        trackPagerView = view
    }

    override fun dropView() {
        trackPagerView = null
    }

    override fun start() {
        trackPagerView?.setCurrentPage(getTodaysPage())
    }

    override fun pageChanged() {
        checkIfPhotoExists()
    }

    private fun checkIfPhotoExists() {
        if (getPhotoFile().exists()) {
            trackPagerView?.showPhotoMenuOptions()
        } else {
            trackPagerView?.hidePhotoMenuOptions()
        }
    }

    override fun onCalendarButtonClicked() {
        val currentDate = TimeUtils.getCalendarForPosition(trackPagerView?.getCurrentPage() ?: 0)
        trackPagerView?.showDatePickerDialog(currentDate)
    }

    override fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val newDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val newPage = TimeUtils.getPositionForDate(LocalDate.fromCalendarFields(newDate))
        trackPagerView?.setCurrentPage(newPage)
    }

    override fun onLeftButtonClicked() {
        trackPagerView?.let {
            it.setCurrentPage(it.getCurrentPage() - 1)
        }
    }

    override fun onRightButtonClicked() {
        trackPagerView?.let {
            it.setCurrentPage(it.getCurrentPage() + 1)
        }
    }

    private fun getTodaysPage() = TimeUtils.getPositionForDate(TimeUtils.today())

    override fun onPhotoItemSelected() {
        trackPagerView?.showPhotoChooserDialog()
    }

    override fun onPhotoFromCameraSelected() {
        trackPagerView?.makePhoto(getPhotoFile())
    }

    override fun onPhotoFromGallerySelected() {
        trackPagerView?.openGallery()
    }

    override fun onPhotoChosen(inputStream: InputStream) {
        trackRepository.createIfNotExists(getCurrentDiaryDate()) {
            val photoFile = getPhotoFile()
            copyToFile(inputStream, photoFile)
            invalidatePhoto(photoFile)
            updateView()
        }
    }

    private fun copyToFile(inputStream: InputStream, photoFile: File) {
        inputStream.use { input ->
            photoFile.outputStream().use { fileOut ->
                input.copyTo(fileOut)
            }
        }
    }

    override fun onPhotoCreated() {
        trackRepository.createIfNotExists(getCurrentDiaryDate()) {
            invalidatePhoto(getPhotoFile())
            updateView()
        }
    }

    override fun onRemoveDaySelected() {
        trackRepository.deleteTrackForDate(getCurrentDiaryDate()) {
            val photo = getPhotoFile()
            if (photo.exists()) {
                invalidatePhoto(photo)
                photo.delete()
            }
            updateView()
        }
    }

    override fun onSharePhotoSelected() {
        val photo = getPhotoFile()
        if (photo.exists()) {
            trackPagerView?.shareFile(photo)
        }
    }

    override fun onMakePhotoVisibleSelected() {
        val photo = getPhotoFile()
        if (photo.exists()) {
            trackPagerView?.makePhotoVisible(photo)
        }
    }

    private fun invalidatePhoto(photo: File) {
        Picasso.get().invalidate(photo)
    }

    private fun updateView() {
        checkIfPhotoExists()
        trackPagerView?.run { updateWholeView() }
    }

    override fun onParametersUpdated() {
        trackPagerView?.run { updateWholeView() }
    }

    private fun getPhotoFile(): File {
        return photoFileConstructor.getForDate(getCurrentDiaryDate())
    }

    private fun getCurrentDiaryDate() =
        TimeUtils.getDateForPosition(trackPagerView?.getCurrentPage() ?: 0)

    override fun onNoteItemSelected() {
        val trackNote = trackRepository.getTrackByDate(getCurrentDiaryDate())?.note
        if (trackNote == null) {
            trackPagerView?.showNoteCreateDialog()
        } else {
            trackPagerView?.showNoteEditDialog(trackNote)
        }
    }

    override fun onCreateNewNote(note: String) {
        saveNote(note)
    }

    override fun onEditNote(note: String) {
        saveNote(note)
    }

    private fun saveNote(note: String) {
        val date = getCurrentDiaryDate()
        val trackExists = trackRepository.getTrackByDate(date) != null
        trackRepository.saveNote(getCurrentDiaryDate(), note) {
            if (trackExists) {
                trackPagerView?.updateNote(TrackData(date, note, arrayOf()))
            } else {
                trackPagerView?.updateWholeView()
            }
        }
    }

    override fun onParametersItemSelected() {
        trackPagerView?.openParametersScreen(getCurrentDiaryDate())
    }
}