package ru.aipova.skintracker.ui.tracktype

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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
    override fun onCreateNewTrackType(trackTypeName: String) {
        realm.executeTransaction {
            it.insert(TrackType().apply {
                name = trackTypeName
            })
        }
    }

    override fun onEditTrackType(trackType: TrackType, trackTypeName: String) {
        realm.executeTransaction {
            trackType.name = trackTypeName
        }
    }

    fun onRemoveTrackType(trackType: TrackType) {
        realm.executeTransaction {
            trackType.deleteFromRealm()
        }
    }

    private lateinit var realm: Realm
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        realm = Realm.getDefaultInstance()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.track_type_fragment, container, false)
        recyclerView = view.findViewById(R.id.track_type_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = TrackTypeAdapter(allTrackTypesAsync(), trackTypeCallbacks)
        return view
    }

    private val trackTypeCallbacks = object : TrackTypeAdapter.Callbacks {
        override fun onTrackTypeEdit(trackType: TrackType) {
            TrackTypeEditDialog.newInstance(trackType).show(childFragmentManager, TRACK_TYPE_EDIT_DIALOG)
        }

        override fun onTrackTypeRemove(trackType: TrackType) {
//            TODO add param to string
            val dialog = AlertDialog.Builder(activity as Context).setMessage(R.string.message_remove_track_type)
                .setPositiveButton(android.R.string.ok, { dialog, which -> onRemoveTrackType(trackType) })
                .setNegativeButton(android.R.string.cancel, null)
                .create()
            dialog.show()
        }

    }

    private fun allTrackTypesAsync(): RealmResults<TrackType> {
        return realm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        track_type_add_fab.setOnClickListener {
            TrackTypeCreateDialog.newInstance().show(childFragmentManager, TRACK_TYPE_CREATE_DIALOG)
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
