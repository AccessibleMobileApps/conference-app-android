package com.robinkanatzar.conference.ui.chooseconference

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.ui.common.FirestoreAdapter
import com.robinkanatzar.conference.util.DateUtils
import com.robinkanatzar.conference.vo.Conference
import kotlinx.android.synthetic.main.choose_conference_item.view.*

open class ConferenceAdapter(query: Query, private val listener: OnConferenceSelectedListener) :
        FirestoreAdapter<ConferenceAdapter.ViewHolder>(query) {

    interface OnConferenceSelectedListener {
        fun onConferenceSelected(conference: DocumentSnapshot, requires_login: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConferenceAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.choose_conference_item, parent, false))
    }

    override fun onBindViewHolder(holder: ConferenceAdapter.ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                snapshot: DocumentSnapshot,
                listener: OnConferenceSelectedListener?
        ) {
            val conference = snapshot.toObject(Conference::class.java)
            if (conference == null) {
                return
            }

            itemView.tv_conference_item_title.text = conference.name

            if (conference.logo == null) {
                itemView.iv_conference_item_image.visibility = View.GONE
            } else {
                itemView.iv_conference_item_image.visibility = View.VISIBLE
                Glide.with(itemView.iv_conference_item_image.context)
                        .load(conference.logo)
                        .into(itemView.iv_conference_item_image)
            }

            itemView.tv_conference_item_date.text = conference.date_start?.let {
                var dateString = DateUtils.toScheduleHeaderFormat(it)
                conference.date_end?.let {
                    dateString = "${dateString} - ${DateUtils.toScheduleHeaderFormat(it)}"
                }
                dateString
            }

            itemView.setOnClickListener {
                if (conference.requires_login == true) {
                    listener?.onConferenceSelected(snapshot, true)
                } else {
                    listener?.onConferenceSelected(snapshot, false)
                }
            }
        }
    }
}
