package com.robinkanatzar.conference.vo

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
data class Partner(
        var conferenceId: String? = null,
        var title: String? = null,
        var content: String? = null,
        var logo: String? = null,
        @ServerTimestamp var date: Date? = null,
        var type: String? = null,
        var content_type: String? = null,
        var url: String? = null
) {

    companion object {
        const val TYPE_SHORT = "short"
        const val TYPE_LONG = "long"
        const val TYPE_HTML = "html"
        const val TYPE_NORMAL = "normal"
    }
}