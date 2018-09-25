package ru.aipova.skintracker.ui.trackpager

import ru.aipova.skintracker.ui.BasePresenter
import ru.aipova.skintracker.ui.BaseView
import java.io.File
import java.util.*

interface TrackPagerContract {
    interface View: BaseView<Presenter> {
        fun getCurrentPage(): Int

        fun showDatePickerDialog(calendar: Calendar)
        fun setCurrentPage(newPage: Int)
        fun makePhoto(photoFile: File)
        fun showNoteCreateDialog()
        fun showNoteEditDialog(trackNote: String)
        fun updateView()
        fun openParametersScreen(date: Date)
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
    }
}