package ru.aipova.skintracker.ui.tracktype

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.DaggerFragment
import io.realm.RealmResults
import kotlinx.android.synthetic.main.track_type_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.di.ActivityScoped
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.ValueType
import ru.aipova.skintracker.ui.tracktype.dialog.TrackTypeCreateDialog
import ru.aipova.skintracker.ui.tracktype.dialog.TrackTypeEditDialog
import javax.inject.Inject

@ActivityScoped
class TrackTypeFragment : DaggerFragment(), TrackTypeContract.View, TrackTypeCreateDialog.Callbacks, TrackTypeEditDialog.Callbacks {
    @Inject
    override lateinit var presenter: TrackTypeContract.Presenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        presenter.takeView(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.dropView()
    }

    override fun onCreateNewTrackType(
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    ) {
        presenter.createNewTrackType(trackTypeName, selectedValueType, min, max)
    }

    override fun onEditTrackType(
        trackType: TrackType,
        trackTypeName: String,
        selectedValueType: ValueType,
        min: Int,
        max: Int
    ) {
        presenter.editTrackTypeName(trackType.uuid, trackTypeName, selectedValueType, min, max)
    }

    fun onRemoveTrackType(trackTypeUid: String) {
        presenter.removeTrackType(trackTypeUid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        presenter.stop()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.track_type_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        trackTypeAddFab.setOnClickListener {
            TrackTypeCreateDialog.newInstance().show(childFragmentManager, TRACK_TYPE_CREATE_DIALOG)
        }
        trackTypeRecycler.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
        presenter.start()
    }

    override fun initTrackTypesView(trackTypes: RealmResults<TrackType>) {
        trackTypeRecycler.adapter = TrackTypeAdapter(trackTypes, trackTypeCallbacks)
    }

    override fun showNoTrackTypesView() {
        noTrackTypesLayout.visibility = View.VISIBLE
        trackTypeRecycler.visibility = View.GONE
    }

    override fun showTrackTypesView() {
        noTrackTypesLayout.visibility = View.GONE
        trackTypeRecycler.visibility = View.VISIBLE
    }

    override fun showTrackTypeCreatedMsg(trackTypeName: String) {
        Toast.makeText(activity, getString(R.string.msg_track_type_created, trackTypeName), Toast.LENGTH_LONG).show()
    }

    override fun showCannotCreateTrackTypeMsg() {
        Toast.makeText(activity, getString(R.string.err_cannot_create_track_type), Toast.LENGTH_LONG).show()
    }

    private val trackTypeCallbacks = object : TrackTypeAdapter.Callbacks {
        override fun onTrackTypeEdit(trackType: TrackType) {
            TrackTypeEditDialog.newInstance(trackType).show(childFragmentManager, TRACK_TYPE_EDIT_DIALOG)
        }

        override fun onTrackTypeRemove(trackType: TrackType) {
            val dialog = AlertDialog.Builder(activity as Context)
                .setMessage(getString(R.string.msg_remove_track_type, trackType.name))
                .setPositiveButton(android.R.string.ok, { dialog, which -> onRemoveTrackType(trackType.uuid) })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
            dialog.show()
        }
    }


    companion object {
        const val TRACK_TYPE_CREATE_DIALOG = "TrackTypeCreateDialog"
        const val TRACK_TYPE_EDIT_DIALOG = "TrackTypeEitDialog"
        fun newInstance(): TrackTypeFragment {
            return TrackTypeFragment()
        }
    }
}
