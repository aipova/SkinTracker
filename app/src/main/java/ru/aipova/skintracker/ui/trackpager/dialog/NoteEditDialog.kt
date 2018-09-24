package ru.aipova.skintracker.ui.trackpager.dialog

import android.content.Context
import android.os.Bundle
import ru.aipova.skintracker.R

class NoteEditDialog : NoteDialog() {
    interface Callbacks {
        fun onEditNote(note: String)
    }

    private var callback: Callbacks? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is Callbacks) {
            callback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onOkButtonClick(note: String) {
        callback?.onEditNote(note)
    }

    private fun getNote() = arguments?.getSerializable(NOTE) as String

    override fun getTitle(): Int {
        return R.string.title_edit_note
    }

    override fun getNoteText(): String? {
        return getNote()
    }

    companion object {
        private const val NOTE = "ru.aipova.skintracker.NOTE"
        fun newInstance(note: String): NoteEditDialog {
            val bundle = Bundle().apply { putSerializable(NOTE, note)}
            return NoteEditDialog()
                .apply { arguments = bundle }
        }
    }
}