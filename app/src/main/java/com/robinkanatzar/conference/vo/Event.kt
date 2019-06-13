package com.robinkanatzar.conference.vo

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
data class Event(
        var conferenceId: String? = null,
        var title: String? = null,
        var content: String? = null,
        var logo: String? = null,
        var order: Int? = null,
        @ServerTimestamp var date_start: Date? = null,
        @ServerTimestamp var date_end: Date? = null,
        var type: String? = null,
        var location: String? = null,
        var speaker: String? = null
) {

    companion object {
        const val TYPE_SHORT = "short"
        const val TYPE_LONG = "long"
        const val TYPE_HEADER = "header"
    }
}