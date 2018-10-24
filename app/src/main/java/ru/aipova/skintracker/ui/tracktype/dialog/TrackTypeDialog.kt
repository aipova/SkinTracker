package ru.aipova.skintracker.ui.tracktype.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.constraint.Group
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import butterknife.BindView
import butterknife.ButterKnife
import com.travijuu.numberpicker.library.NumberPicker
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.ValueType

abstract class TrackTypeDialog : DialogFragment() {
    private lateinit var dialog: AlertDialog

    @BindView(R.id.trackTypeNameEditTxt)
    lateinit var trackTypeNameEditText: TextInputEditText

    @BindView(R.id.trackTypeNameTxtLayout)
    lateinit var trackTypeNameLayout: TextInputLayout

    @BindView(R.id.trackTypeSpinner)
    lateinit var trackTypeSpinner: Spinner

    @BindView(R.id.rangeGroup)
    lateinit var rangeGroup: Group

    @BindView(R.id.fromNumberPicker)
    lateinit var fromNumber: NumberPicker

    @BindView(R.id.toNumberPicker)
    lateinit var toNumber: NumberPicker

    lateinit var valueTypes: Map<String, ValueType>
    private var selectedValueType: ValueType = ValueType.SEEK

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.track_type_dialog, null)
        valueTypes = mapOf(
            getString(R.string.range) to ValueType.SEEK,
            getString(R.string.yes_no) to ValueType.BOOLEAN,
            getString(R.string.amount) to ValueType.AMOUNT
        )
        ButterKnife.bind(this, view)
        trackTypeNameEditText.setText(getNameText())
        setupSpinner()
        fromNumber.value = getMinValue()
        toNumber.value = getMaxValue()

        dialog = AlertDialog.Builder(activity as Context)
            .setView(view)
            .setTitle(getTitle())
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        return dialog
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_spinner_item,
            valueTypes.keys.toTypedArray()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        trackTypeSpinner.adapter = adapter
        val position =
            adapter.getPosition(valueTypes.entries.find { it.value == getValueType() }!!.key)
        trackTypeSpinner.setSelection(position)
        setRangeSettingsVisibility(getValueType())
        trackTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedValueType = valueTypes[adapter.getItem(position)] ?: ValueType.SEEK
                setRangeSettingsVisibility(selectedValueType)
            }
        }

    }

    private fun setRangeSettingsVisibility(valueType: ValueType) {
        if (valueType == ValueType.SEEK) {
            rangeGroup.visibility = View.VISIBLE
        } else {
            rangeGroup.visibility = View.GONE
        }
    }

    protected open fun getNameText(): String? {
        return null
    }

    protected open fun getValueType(): ValueType {
        return ValueType.SEEK
    }

    protected open fun getMinValue(): Int {
        return SEEK_BAR_MIN
    }

    protected open fun getMaxValue(): Int {
        return SEEK_BAR_MAX
    }

    override fun onResume() {
        super.onResume()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val trackTypeName = trackTypeNameEditText.text.toString()
            if (trackTypeName.isBlank()) {
                trackTypeNameLayout.error = getString(R.string.err_track_type_empty_name)
            } else if (nameIsDuplicate(trackTypeName)) {
                trackTypeNameLayout.error = getString(R.string.err_track_type_duplicate_name)
            } else {
                onOkButtonClick(
                    trackTypeNameEditText.text.toString(),
                    selectedValueType,
                    fromNumber.value,
                    toNumber.value
                )
                dialog.dismiss()
            }
        }
    }

    protected abstract fun nameIsDuplicate(trackTypeName: String): Boolean

    protected abstract fun onOkButtonClick(
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    )

    protected abstract fun getTitle(): Int

    companion object {
        const val SEEK_BAR_MAX = 10
        const val SEEK_BAR_MIN = 0
    }
}