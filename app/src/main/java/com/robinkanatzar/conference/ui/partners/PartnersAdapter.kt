package com.robinkanatzar.conference.ui.partners

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.ui.common.FirestoreAdapter
import com.robinkanatzar.conference.vo.Partner
import kotlinx.android.synthetic.main.partners_item.view.*

open class PartnersAdapter(query: Query, private val listener: OnPartnerSelectedListener) :
        FirestoreAdapter<PartnersAdapter.ViewHolder>(query) {

    interface OnPartnerSelectedListener {
        fun onPartnerSelected(partner: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartnersAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.partners_item, parent, false))
    }

    override fun onBindViewHolder(holder: PartnersAdapter.ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                snapshot: DocumentSnapshot,
                listener: PartnersAdapter.OnPartnerSelectedListener?
        ) {
            val partner = snapshot.toObject(Partner::class.java)
            if (partner == null) {
                return
            }

            if (partner.logo == null) {
                itemView.iv_partners_item_image.visibility = View.GONE
            } else {
                itemView.iv_partners_item_image.visibility = View.VISIBLE
                Glide.with(itemView.iv_partners_item_image.context)
                        .load(partner.logo)
                        .into(itemView.iv_partners_item_image)
            }

            itemView.setOnClickListener {
                if (!partner.type.equals(Partner.TYPE_SHORT)) {
                    listener?.onPartnerSelected(snapshot)
                }
            }
        }
    }
}