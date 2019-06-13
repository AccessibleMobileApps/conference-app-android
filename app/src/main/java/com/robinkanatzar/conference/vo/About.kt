package com.robinkanatzar.conference.vo

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class About(
        var conferenceId: String? = null,
        var title: String? = null,
        var content: String? = null,
        var logo:String? = null,
        var content_type: String? = null,
        var url: String? = null
) {

    companion object {
        const val TYPE_HTML = "html"
        const val TYPE_NORMAL = "normal"
    }
}