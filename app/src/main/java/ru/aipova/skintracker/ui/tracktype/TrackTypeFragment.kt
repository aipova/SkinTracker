package ru.aipova.skintracker.ui.tracktype

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import io.realm.Realm
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.tracktype_fragment.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.TrackTypeFields

class TrackTypeFragment : Fragment(), TrackTypeDialog.Callbacks {
    override fun onCreateNewTrackType(newTrackTypeName: String) {
        realm.executeTransaction {
            it.insert(TrackType().apply {
                name = newTrackTypeName
            })
        }
//        TODO use recycler view with realm adapter
    }

    private lateinit var trackTypes: RealmResults<TrackType>
    private lateinit var realm: Realm
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        retainInstance = true
        realm = Realm.getDefaultInstance()
        trackTypes = realm.where<TrackType>().equalTo(TrackTypeFields.REMOVABLE, true).findAll()
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
        layout = view.findViewById(R.id.tracktype_layout)


        for ((index, trackType) in trackTypes.withIndex()) {
            val tv = TextView(activity)
            tv.text = trackType.name
            layout.addView(tv)
        }
        return view
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
