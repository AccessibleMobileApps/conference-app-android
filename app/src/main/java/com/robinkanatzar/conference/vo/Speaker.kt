package com.robinkanatzar.conference.vo

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Speaker(
        var conferenceId: String? = null,
        var first_name: String? = null,
        var last_name: String? = null,
        var content: String? = null,
        var logo: String? = null,
        var tag_line: String? = null
)