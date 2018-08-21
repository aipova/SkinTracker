package ru.aipova.skintracker.ui.track

import ru.aipova.skintracker.model.Track
import ru.aipova.skintracker.model.source.TrackRepository
import java.text.SimpleDateFormat
import java.util.*

class TrackPresenter(
    private var trackView: TrackContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository
) : TrackContract.Presenter {
    init {
        trackView.presenter = this
        currentDate = truncateToDay(currentDate)
    }

    override fun start() {
        trackView.loadPhoto(getPhotoFileName())
        loadTrackValues()
        showTrackNote(trackRepository.getTrackByDate(currentDate))
    }

    private fun loadTrackValues() {
        val trackValues = trackRepository.getTrackValuesData(currentDate)
        trackView.showTrackValues(trackValues)
    }

    private fun showTrackNote(existingTrack: Track?) {
        existingTrack?.note?.let {
            trackView.setupNoteText(it)
        }
    }

    override fun photoCalled() {
        trackView.makePhoto(getPhotoFileName())
    }

    override fun photoCreated() {
        trackView.loadPhoto(getPhotoFileName())
    }

    override fun save() {
        val trackData = TrackData(currentDate, trackView.getNote(), trackView.getTrackValueData())
        trackRepository.createOrUpdate(trackData, object : TrackRepository.CreateTrackCallback {
            override fun onTrackCreated() {
                if (!trackView.isActive) return
                trackView.showTrackCreatedMsg()
                trackView.close()
            }

            override fun onError() {
                if (!trackView.isActive) return
                trackView.showCannotCreateTrackMsg()
            }
        })

    }

    private fun getPhotoFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd").format(currentDate)
        return "ST_$timeStamp.jpg"
    }

    private fun truncateToDay(date: Date): Date {
        return Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }
}