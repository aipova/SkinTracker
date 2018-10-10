package ru.aipova.skintracker

import android.content.Context
import io.realm.Realm
import ru.aipova.skintracker.model.TrackType
import ru.aipova.skintracker.model.ValueType

class InitialData(val context: Context) : Realm.Transaction {
    override fun execute(realm: Realm) {
        realm.insertOrUpdate(initialTrackTypes())
    }

    private fun initialTrackTypes() =
        listOf(skinQualityTrackType, pimplesCountTrackType, sugarTrackType, makeupTrackType)

    private val  skinQualityTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = TrackType.SKIN_QUALITY_TRACK_TYPE_UID
                name = context.getString(R.string.track_type_name_skin_quality)
                setValueTypeEnum(ValueType.SEEK)
            }
        }

    private val pimplesCountTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = TrackType.PIMPLES_COUNT_TRACK_TYPE_UID
                name = context.getString(R.string.track_type_name_pimples_amount)
                setValueTypeEnum(ValueType.AMOUNT)
            }
        }

    private val  sugarTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = TrackType.SUGAR_TRACK_TYPE_UID
                name = context.getString(R.string.track_type_name_sugar)
                setValueTypeEnum(ValueType.AMOUNT)
            }
        }

    private val makeupTrackType: TrackType
        get() {
            return TrackType().apply {
                uuid = TrackType.MAKEUP_TRACK_TYPE_UID
                name = context.getString(R.string.track_type_name_makeup)
                setValueTypeEnum(ValueType.BOOLEAN)
            }
        }
}