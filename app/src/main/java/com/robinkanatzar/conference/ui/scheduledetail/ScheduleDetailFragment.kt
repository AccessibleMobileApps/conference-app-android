package com.robinkanatzar.conference.ui.scheduledetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.DateUtils
import com.robinkanatzar.conference.vo.Event
import kotlinx.android.synthetic.main.schedule_detail_fragment.*

class ScheduleDetailFragment : Fragment(), Injectable, EventListener<DocumentSnapshot> {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private lateinit var scheduleRef: DocumentReference
    private var scheduleRegistration: ListenerRegistration? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val safeArgs: ScheduleDetailFragmentArgs by navArgs()
        val eventId = safeArgs.eventId

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }
        firestore = FirebaseFirestore.getInstance()

        scheduleRef = firestore.collection(Constants.FB_SCHEDULE).document(eventId)

        return inflater.inflate(R.layout.schedule_detail_fragment, null)
    }

    override fun onStart() {
        super.onStart()
        scheduleRegistration = scheduleRef.addSnapshotListener(this)
    }

    override fun onStop() {
        super.onStop()
        scheduleRegistration?.remove()
        scheduleRegistration = null
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            return
        }

        snapshot?.let {
            val event = snapshot.toObject(Event::class.java)
            if (event != null) {
                onEventLoaded(event)
            }
        }
    }

    private fun onEventLoaded(event: Event) {
        tv_schedule_detail_title.text = event.title
        tv_schedule_detail_content.text = event.content

        if (event.location == null) {
            tv_schedule_detail_location_label.visibility = View.GONE
            tv_schedule_detail_location.visibility = View.GONE
        } else {
            tv_schedule_detail_location.text = event.location
            tv_schedule_detail_location_label.visibility = View.VISIBLE
            tv_schedule_detail_location.visibility = View.VISIBLE
        }

        if (event.speaker == null) {
            tv_schedule_detail_speaker.visibility = View.GONE
            tv_schedule_detail_speaker_label.visibility = View.GONE
        } else {
            tv_schedule_detail_speaker.text = event.speaker
            tv_schedule_detail_speaker.visibility = View.VISIBLE
            tv_schedule_detail_speaker_label.visibility = View.VISIBLE
        }

        if (event.date_start == null) {
            tv_schedule_detail_time.visibility = View.GONE
            tv_schedule_detail_time_label.visibility = View.GONE
        } else {
            tv_schedule_detail_time.visibility = View.VISIBLE
            tv_schedule_detail_time_label.visibility = View.VISIBLE

            tv_schedule_detail_time.text = event.date_start?.let {
                var dateString = DateUtils.toScheduleCellFormat(it)
                event.date_end?.let {
                    dateString = "${dateString} - ${DateUtils.toScheduleCellFormat(it)}"
                }
                dateString
            }
        }

        if (event.logo == null) {
            iv_schedule_detail_item_image.visibility = View.GONE
        } else {
            iv_schedule_detail_item_image.visibility = View.VISIBLE
            Glide.with(iv_schedule_detail_item_image.context)
                    .load(event.logo)
                    .into(iv_schedule_detail_item_image)
        }
    }
}