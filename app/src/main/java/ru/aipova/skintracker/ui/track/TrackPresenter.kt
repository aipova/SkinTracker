package ru.aipova.skintracker.ui.track

import io.realm.RealmList
import ru.aipova.skintracker.model.source.TrackRepository
import ru.aipova.skintracker.model.source.TrackTypeRepository
import java.text.SimpleDateFormat
import java.util.*

class TrackPresenter(
    private val trackView: TrackContract.View,
    private var currentDate: Date,
    private val trackRepository: TrackRepository,
    private val trackTypeRepository: TrackTypeRepository
) : TrackContract.Presenter {
    init {
        trackView.presenter = this
        currentDate = truncateToDay(currentDate)
    }

    override fun start() {

        val trackTypes = trackTypeRepository.getTrackTypesSnapshot().toTypedArray()
        val existingTrack = trackRepository.getTrackByDate(currentDate)

        val existingTracks = existingTrack?.values ?: RealmList()
        val trackValues = trackTypes.map { trackType ->
            val existingTrackValue = existingTracks.find { it.trackType == trackType }
            val value = existingTrackValue?.value?.toInt() ?: 0
            TrackValueData(trackType.uuid, trackType.name ?: "", value)
        }.toTypedArray()

        trackView.initTrackValues(trackValues)
        existingTrack?.note?.let {
            trackView.setupNoteText(it)
        }
    }

    override fun saveTrackData(trackValueDataArray: Array<TrackValueData>, note: String) {
        val trackData = TrackData(currentDate, note, trackValueDataArray)

        trackRepository.createOrUpdate(trackData, object : TrackRepository.CreateTrackCallback {
            override fun onTrackCreated() {
                trackView.showTrackCreatedMsg()
            }
            override fun onError() {
                trackView.showCannotCreateTrackMsg()
            }
        })

    }

    override fun getPhotoFileName(): String {
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