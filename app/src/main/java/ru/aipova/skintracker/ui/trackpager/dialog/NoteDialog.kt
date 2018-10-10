package ru.aipova.skintracker.ui.trackpager.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import butterknife.BindView
import butterknife.ButterKnife
import ru.aipova.skintracker.R

abstract class NoteDialog : DialogFragment() {
    private lateinit var dialog: AlertDialog

    @BindView(R.id.noteEditTxt)
    lateinit var noteEditText: TextInputEditText

    @BindView(R.id.noteTxtLayout)
    lateinit var noteTxtLayout: TextInputLayout

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.note_dialog, null)

        ButterKnife.bind(this, view)
        noteEditText.setText(getNoteText())
        dialog = AlertDialog.Builder(activity as Context)
            .setView(view)
            .setTitle(getTitle())
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        return dialog
    }

    protected open fun getNoteText(): String? {
        return null
    }

    override fun onResume() {
        super.onResume()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val trackTypeName = noteEditText.text.toString()
            if (trackTypeName.isBlank()) {
                noteTxtLayout.error = getString(R.string.err_note_empty)
            } else {
                onOkButtonClick(noteEditText.text.toString())
                dialog.dismiss()
            }
        }
    }

    protected abstract fun onOkButtonClick(note: String)

    protected abstract fun getTitle(): Int
}