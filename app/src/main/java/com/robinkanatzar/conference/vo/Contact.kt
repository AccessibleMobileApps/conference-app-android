package com.robinkanatzar.conference.vo

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Contact(
        var first_name: String? = null,
        var last_name: String? = null,
        var email: String? = null,
        var text: String? = null,
        var conference_id: String? = null,
        @ServerTimestamp var timestamp: Date? = null
) {

        constructor(first_name: String, last_name: String, email: String, text: String, conference_id: String) : this() {
                this.conference_id = conference_id
                this.first_name = first_name
                this.last_name = last_name
                this.email = email
                this.text = text
        }
}