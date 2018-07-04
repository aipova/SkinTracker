package ru.aipova.skintracker.ui.tracktype

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.tracktype_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackTypeFields

class TrackTypeFragment : Fragment(), TrackTypeDialog.Callbacks {
    override fun onCreateNewTrackType(trackTypeName: String) {
        realm.executeTransactionAsync {
            it.insert(TrackType().apply {
                name = trackTypeName
            })
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
        val view = inflater.inflate(R.layout.tracktype_fragment, container, false)
        recyclerView = view.findViewById(R.id.tracktype_recycler)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = TrackTypeAdapter(allTrackTypesAsync())
        return view
    }

    private fun allTrackTypesAsync(): RealmResults<TrackType> {
        return realm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAllAsync()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        add_tracktype.setOnClickListener {
            TrackTypeDialog().show(childFragmentManager, TRACK_TYPE_DIALOG)
        }
    }

    companion object {
        const val TRACK_TYPE_DIALOG = "TrackTypeDialog"
        fun newInstance(): TrackTypeFragment {
            return TrackTypeFragment()
        }
    }
}
