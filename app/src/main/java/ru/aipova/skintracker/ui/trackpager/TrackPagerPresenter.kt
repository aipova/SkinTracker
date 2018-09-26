package ru.aipova.skintracker.ui.trackpager

import com.squareup.picasso.Picasso
import org.joda.time.LocalDate
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.ui.data.TrackData
import ru.aipova.skintracker.utils.PhotoFileConstructor
import ru.aipova.skintracker.utils.TimeUtils
import java.io.File
import java.util.*

class TrackPagerPresenter(
    private var trackPagerView: TrackPagerContract.View,
    private val trackRepository: TrackRepository,
    private val photoFileConstructor: PhotoFileConstructor
) : TrackPagerContract.Presenter {
    init {
        trackPagerView.presenter = this
    }


    override fun start() {
        trackPagerView.setCurrentPage(getTodaysPage())
    }

    override fun onCalendarButtonClicked() {
        val currentDate = TimeUtils.getCalendarForPosition(trackPagerView.getCurrentPage())
        trackPagerView.showDatePickerDialog(currentDate)
    }

    override fun onDateSelected(year: Int, month: Int, dayOfMonth: Int) {
        val newDate = Calendar.getInstance().apply { set(year, month, dayOfMonth) }
        val newPage = TimeUtils.getPositionForDate(LocalDate.fromCalendarFields(newDate))
        trackPagerView.setCurrentPage(newPage)
    }

    override fun onLeftButtonClicked() {
        trackPagerView.setCurrentPage(trackPagerView.getCurrentPage() - 1)
    }

    override fun onRightButtonClicked() {
        trackPagerView.setCurrentPage(trackPagerView.getCurrentPage() + 1)
    }

    private fun getTodaysPage() = TimeUtils.getPositionForDate(TimeUtils.today())

    override fun onPhotoItemSelected() {
        trackPagerView.makePhoto(getPhotoFile())
    }

    override fun onPhotoCreated() {
        trackRepository.createIfNotExists(getCurrentDiaryDate()) {
            Picasso.get().invalidate(getPhotoFile())
            trackPagerView.updateWholeView()
        }
    }

    override fun onParametersUpdated() {
        trackPagerView.updateWholeView()
    }

    private fun getPhotoFile(): File {
        return photoFileConstructor.getForDate(getCurrentDiaryDate())
    }

    private fun getCurrentDiaryDate() =
        TimeUtils.getDateForPosition(trackPagerView.getCurrentPage())

    override fun onNoteItemSelected() {
        val trackNote = trackRepository.getTrackByDate(getCurrentDiaryDate())?.note
        if (trackNote == null) {
            trackPagerView.showNoteCreateDialog()
        } else {
            trackPagerView.showNoteEditDialog(trackNote)
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
                trackPagerView.updateNote(TrackData(date, note, arrayOf()))
            } else {
                trackPagerView.updateWholeView()
            }
        }
    }

    override fun onParametersItemSelected() {
        trackPagerView.openParametersScreen(getCurrentDiaryDate())
    }
}