package ru.aipova.skintracker.ui.tracktype

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.track_type_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackTypeFields

class TrackTypeFragment : Fragment(), TrackTypeCreateDialog.Callbacks, TrackTypeEditDialog.Callbacks {

    private lateinit var realm: Realm

    override fun onCreateNewTrackType(trackTypeName: String) {
        realm.executeTransactionAsync { bgRealm ->
            bgRealm.insert(TrackType().apply {
                name = trackTypeName
            })
        }
    }

    override fun onEditTrackType(trackType: TrackType, trackTypeName: String) {
        val trackTypeUid = trackType.uuid
        realm.executeTransactionAsync { bgRealm ->
            val trackTypeManaged = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackTypeManaged?.name = trackTypeName
        }
    }

    fun onRemoveTrackType(trackTypeUid: String) {
//         TODO remove all tracks or mark removed?
        realm.executeTransactionAsync { bgRealm ->
            val trackType = bgRealm.where<TrackType>().equalTo(TrackTypeFields.UUID, trackTypeUid).findFirst()
            trackType?.deleteFromRealm()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        realm = Realm.getDefaultInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        trackTypeRecycler.adapter = null
        realm.close()
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
            adapter = TrackTypeAdapter(allTrackTypesAsync(), trackTypeCallbacks)
        }
    }


    private val trackTypeCallbacks = object : TrackTypeAdapter.Callbacks {
        override fun onTrackTypeEdit(trackType: TrackType) {
            TrackTypeEditDialog.newInstance(trackType).show(childFragmentManager, TRACK_TYPE_EDIT_DIALOG)
        }

        override fun onTrackTypeRemove(trackType: TrackType) {
            val dialog = AlertDialog.Builder(activity as Context)
                .setMessage(getString(R.string.message_remove_track_type, trackType.name))
                .setPositiveButton(android.R.string.ok, { dialog, which -> onRemoveTrackType(trackType.uuid) })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
            dialog.show()
        }

    }

    private fun allTrackTypesAsync(): RealmResults<TrackType> {
        return realm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }

    companion object {
        const val TRACK_TYPE_CREATE_DIALOG = "TrackTypeCreateDialog"
        const val TRACK_TYPE_EDIT_DIALOG = "TrackTypeEitDialog"
        fun newInstance(): TrackTypeFragment {
            return TrackTypeFragment()
        }
    }
}
