package ru.aipova.skintracker.ui.tracktype

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.track_type_item.*
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType

class TrackTypeAdapter(trackTypes: RealmResults<TrackType>, private val callbacks: Callbacks) :
    RealmRecyclerViewAdapter<TrackType, TrackTypeAdapter.TrackTypeViewHolder>(trackTypes, true) {

    interface Callbacks {
        fun onTrackTypeEdit(trackType: TrackType)
        fun onTrackTypeRemove(trackType: TrackType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_type_item, parent, false)
        return TrackTypeViewHolder(view, callbacks)

    }

    override fun onBindViewHolder(holder: TrackTypeViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(item) }
    }

    inner class TrackTypeViewHolder(override val containerView: View?, private val callbacks: Callbacks)
        : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private var trackType: TrackType? = null
        init {
            trackTypeEditBtn.setOnClickListener { trackType?.let { callbacks.onTrackTypeEdit(it) } }
            trackTypeRemoveBtn.setOnClickListener { trackType?.let { callbacks.onTrackTypeRemove(it) } }
        }

        fun bind(trackType: TrackType) {
            this.trackType = trackType
            trackTypeNameTxt.text = trackType.name
        }

    }
}