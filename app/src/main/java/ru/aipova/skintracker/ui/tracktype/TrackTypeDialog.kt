package ru.aipova.skintracker.ui.tracktype

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import ru.aipova.skintracker.R

abstract class TrackTypeDialog : DialogFragment() {
    private lateinit var dialog: AlertDialog
    private lateinit var trackTypeNameEditText: TextInputEditText
    private lateinit var trackTypeNameLayout: TextInputLayout

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.track_type_dialog, null)
        trackTypeNameEditText = view.findViewById(R.id.track_type_name_edit_txt)
        trackTypeNameEditText.setText(getNameText())
        trackTypeNameLayout = view.findViewById(R.id.track_type_name_txt_layout)
        dialog = AlertDialog.Builder(activity as Context)
            .setView(view)
            .setTitle(getTitle())
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        return dialog
    }

    protected open fun getNameText(): String? {
        return null
    }

    override fun onResume() {
        super.onResume()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val trackTypeName = trackTypeNameEditText.text.toString()
            if (trackTypeName.isBlank()) {
                trackTypeNameLayout.error = getString(R.string.error_track_type_empty_name)
            } else {
                onOkButtonClick(trackTypeNameEditText.text.toString())
                dialog.dismiss()
            }
        }
    }

    protected abstract fun onOkButtonClick(trackTypeName: String)
    protected abstract fun getTitle() : Int
}