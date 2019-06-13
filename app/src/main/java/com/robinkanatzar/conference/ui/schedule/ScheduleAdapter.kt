package com.robinkanatzar.conference.ui.schedule

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
import com.robinkanatzar.conference.vo.Event
import kotlinx.android.synthetic.main.schedule_item.view.*

open class ScheduleAdapter(query: Query, private val listener: OnEventSelectedListener) :
        FirestoreAdapter<ScheduleAdapter.ViewHolder>(query) {

    interface OnEventSelectedListener {
        fun onEventSelected(event: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.schedule_item, parent, false))
    }

    override fun onBindViewHolder(holder: ScheduleAdapter.ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                snapshot: DocumentSnapshot,
                listener: ScheduleAdapter.OnEventSelectedListener?
        ) {
            val event = snapshot.toObject(Event::class.java)
            if (event == null) {
                return
            }

            itemView.tv_schedule_item_title.text = event.title

            if (event.logo == null) {
                itemView.iv_schedule_item_image.visibility = View.GONE
            } else {
                itemView.iv_schedule_item_image.visibility = View.VISIBLE
                Glide.with(itemView.iv_schedule_item_image.context)
                        .load(event.logo)
                        .into(itemView.iv_schedule_item_image)
            }

            if (event.type.equals(Event.TYPE_SHORT)) {
                itemView.cl_schedule_item_card.visibility = View.VISIBLE
                itemView.tv_schedule_item_header.visibility = View.INVISIBLE
                itemView.tv_schedule_item_location.visibility = View.VISIBLE
                itemView.iv_schedule_item_more_arrow.visibility = View.GONE
                itemView.tv_schedule_item_location.text = event.location
                itemView.tv_schedule_item_time.text = event.date_start?.let {
                    var dateString = DateUtils.toScheduleCellFormat(it)
                    event.date_end?.let {
                        dateString = "${dateString} - ${DateUtils.toScheduleCellFormat(it)}"
                    }
                    dateString
                }
            } else if (event.type.equals(Event.TYPE_HEADER)) {
                itemView.cl_schedule_item_card.visibility = View.GONE
                itemView.tv_schedule_item_header.visibility = View.VISIBLE
                itemView.tv_schedule_item_header.text = event.date_start?.let {
                    DateUtils.toScheduleHeaderFormat(it)
                }
            } else {
                itemView.tv_schedule_item_header.visibility = View.INVISIBLE
                itemView.cl_schedule_item_card.visibility = View.VISIBLE
                itemView.tv_schedule_item_location.visibility = View.VISIBLE
                itemView.tv_schedule_item_location.text = event.location
                itemView.iv_schedule_item_more_arrow.visibility = View.VISIBLE
                itemView.tv_schedule_item_time.text = event.date_start?.let {
                    var dateString = DateUtils.toScheduleCellFormat(it)
                    event.date_end?.let {
                        dateString = "${dateString} - ${DateUtils.toScheduleCellFormat(it)}"
                    }
                    dateString
                }
            }

            itemView.cl_schedule_item_card.setOnClickListener {
                if (!event.type.equals(Event.TYPE_SHORT)) {
                    listener?.onEventSelected(snapshot)
                }
            }
        }
    }
}