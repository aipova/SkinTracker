package ru.aipova.skintracker.ui.trackpager

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import ru.aipova.skintracker.ui.data.TrackData
import java.io.File
import java.io.InputStream
import java.util.*

interface TrackPagerContract {
    interface View: BaseView<Presenter> {
        fun getCurrentPage(): Int

        fun showDatePickerDialog(calendar: Calendar)
        fun setCurrentPage(newPage: Int)
        fun makePhoto(photoFile: File)
        fun showNoteCreateDialog()
        fun showNoteEditDialog(trackNote: String)
        fun updateWholeView()
        fun openParametersScreen(date: Date)
        fun updateNote(note: TrackData)
        fun isActive(): Boolean
        fun showPhotoChooserDialog()
        fun openGallery()
    }

    interface Presenter: BasePresenter {
        fun onCalendarButtonClicked()
        fun onDateSelected(year: Int, month: Int, dayOfMonth: Int)
        fun onLeftButtonClicked()
        fun onRightButtonClicked()
        fun onPhotoItemSelected()
        fun onNoteItemSelected()
        fun onCreateNewNote(note: String)
        fun onEditNote(note: String)
        fun onParametersItemSelected()
        fun onPhotoCreated()
        fun onParametersUpdated()
        fun onRemoveDaySelected()
        fun onPhotoFromCameraSelected()
        fun onPhotoFromGallerySelected()
        fun onPhotoChosen(inputStream: InputStream)
        fun takeView(view: View)
        fun dropView()
    }
}