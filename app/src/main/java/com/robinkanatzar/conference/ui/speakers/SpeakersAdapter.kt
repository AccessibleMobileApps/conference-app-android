package com.robinkanatzar.conference.ui.speakers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.ui.common.FirestoreAdapter
import com.robinkanatzar.conference.ui.partners.PartnersAdapter
import com.robinkanatzar.conference.vo.Article
import com.robinkanatzar.conference.vo.Partner
import com.robinkanatzar.conference.vo.Speaker
import kotlinx.android.synthetic.main.partners_item.view.*
import kotlinx.android.synthetic.main.speakers_item.view.*

open class SpeakersAdapter(query: Query, private val listener: OnSpeakerSelectedListener) :
        FirestoreAdapter<SpeakersAdapter.ViewHolder>(query) {

    interface OnSpeakerSelectedListener {
        fun onSpeakerSelected(speaker: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakersAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.speakers_item, parent, false))
    }

    override fun onBindViewHolder(holder: SpeakersAdapter.ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                snapshot: DocumentSnapshot,
                listener: SpeakersAdapter.OnSpeakerSelectedListener?
        ) {
            val speaker = snapshot.toObject(Speaker::class.java)
            if (speaker == null) {
                return
            }

            itemView.tv_speakers_item_title.text = itemView.context.getString(R.string.speakers_first_and_last_name, speaker.first_name, speaker.last_name)
            itemView.tv_speakers_item_tag_line.text = speaker.tag_line

            if (speaker.logo == null) {
                Glide.with(itemView.civ_speakers_item_image.context)
                        .load(R.drawable.avatar)
                        .into(itemView.civ_speakers_item_image)
            } else {
                Glide.with(itemView.civ_speakers_item_image.context)
                        .load(speaker.logo)
                        .into(itemView.civ_speakers_item_image)
            }

            itemView.setOnClickListener {
                listener?.onSpeakerSelected(snapshot)
            }
        }
    }
}