package com.robinkanatzar.conference.vo

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@IgnoreExtraProperties
data class Conference(
        var conference_id: String? = null,
        var name: String? = null,
        var logo: String? = null,
        var order: Int? = null,
        @ServerTimestamp var date_start: Date? = null,
        @ServerTimestamp var date_end: Date? = null,
        var requires_login: Boolean? = false

)