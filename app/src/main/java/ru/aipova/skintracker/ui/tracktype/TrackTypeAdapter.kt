package ru.aipova.skintracker.ui.tracktype

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import ru.aipova.skintracker.R
import ru.aipova.skintracker.model.TrackType

class TrackTypeAdapter(trackTypes: RealmResults<TrackType>) :
    RealmRecyclerViewAdapter<TrackType, TrackTypeAdapter.TrackTypeViewHolder>(trackTypes, true) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackTypeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_type_item, parent, false)
        return TrackTypeViewHolder(view)

    }

    override fun onBindViewHolder(holder: TrackTypeViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.setName(it.name ?: "") }
    }

    inner class TrackTypeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.track_type_name_txt)

        fun setName(name: String) {
            nameTextView.text = name
        }

    }
}