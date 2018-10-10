package ru.aipova.skintracker.ui.trackpager.dialog

import android.content.Context
import ru.aipova.skintracker.R

class NoteCreateDialog : NoteDialog() {
    private var callback: Callbacks? = null

    interface Callbacks {
        fun onCreateNewNote(note: String)
    }

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
        callback?.onCreateNewNote(note)
    }

    override fun getTitle(): Int {
        return R.string.title_new_note

    }

    companion object {
        fun newInstance(): NoteCreateDialog {
            return NoteCreateDialog()
        }
    }
}