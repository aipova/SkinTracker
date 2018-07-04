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

class TrackTypeDialog : DialogFragment() {
    private lateinit var dialog: AlertDialog
    private lateinit var trackTypeNameEditText: TextInputEditText
    private lateinit var trackTypeNameLayout: TextInputLayout

    interface Callbacks {
        fun onCreateNewTrackType(trackTypeName: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.tracktype_dialog, null)
        trackTypeNameEditText = view.findViewById(R.id.trackTypeName)
        trackTypeNameLayout = view.findViewById(R.id.trackTypeNameLayout)
        dialog = AlertDialog.Builder(activity as Context)
            .setView(view)
            .setTitle(R.string.new_track_type_title)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        return dialog
    }

    override fun onResume() {
        super.onResume()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val trackTypeName = trackTypeNameEditText.text.toString()
            if (trackTypeName.isBlank()) {
                trackTypeNameLayout.error = getString(R.string.enter_name)
            } else {
                parentFragment.let {
                    if (it is Callbacks) {
                        it.onCreateNewTrackType(trackTypeNameEditText.text.toString())
                        dialog.dismiss()
                    }
                }
            }
        }
    }
}