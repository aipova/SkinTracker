package ru.aipova.skintracker.ui.tracktype

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.widget.EditText
import ru.aipova.skintracker.R

class TrackTypeDialog : DialogFragment() {

    interface Callbacks {
        fun onCreateNewTrackType(name: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.tracktype_dialog, null)
        val trackTypeNameEditText = view.findViewById<EditText>(R.id.trackTypeName)
        return AlertDialog.Builder(activity as Context)
            .setView(view)
            .setTitle(R.string.new_track_type_title)
            .setPositiveButton(android.R.string.ok, { dialog, which ->
                parentFragment.let {
                    if (it is Callbacks) {
                        it.onCreateNewTrackType(trackTypeNameEditText.text.toString())
                    }
                }
            }).create()
    }
}