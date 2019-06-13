package com.robinkanatzar.conference.ui.partnersdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.MyWebViewClient
import com.robinkanatzar.conference.vo.Partner
import kotlinx.android.synthetic.main.partners_detail_fragment.*

class PartnersDetailFragment : Fragment(), Injectable, EventListener<DocumentSnapshot> {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private lateinit var partnerRef: DocumentReference
    private var partnerRegistration: ListenerRegistration? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val safeArgs: PartnersDetailFragmentArgs by navArgs()
        val partnerId = safeArgs.partnerId

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        partnerRef = firestore.collection(Constants.FB_PARTNERS).document(partnerId)

        return inflater.inflate(R.layout.partners_detail_fragment, null)
    }

    override fun onStart() {
        super.onStart()
        partnerRegistration = partnerRef.addSnapshotListener(this)
    }

    override fun onStop() {
        super.onStop()
        partnerRegistration?.remove()
        partnerRegistration = null
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            return
        }

        snapshot?.let {
            val partner = snapshot.toObject(Partner::class.java)
            if (partner != null) {
                onPartnerLoaded(partner)
            }
        }
    }

    private fun onPartnerLoaded(partner: Partner) {

        tv_partners_detail_content.text = partner.content

        if (partner.content_type.equals(Partner.TYPE_HTML)) {
            wv_partners_detail.visibility = View.VISIBLE
            sv_partners_detail_normal.visibility = View.GONE
            wv_partners_detail.webViewClient = MyWebViewClient(this)

            partner.url?.let {
                wv_partners_detail.loadUrl(partner.url.toString())
            } ?: run {
                wv_partners_detail.loadData(partner.content, "text/html; charset=utf-8", "UTF-8")
            }
        } else {
            wv_partners_detail.visibility = View.GONE
            sv_partners_detail_normal.visibility = View.VISIBLE

            if (partner.logo == null) {
                iv_partners_detail_item_image.visibility = View.GONE
            } else {
                iv_partners_detail_item_image.visibility = View.VISIBLE
                Glide.with(iv_partners_detail_item_image.context)
                        .load(partner.logo)
                        .into(iv_partners_detail_item_image)
            }
        }
    }
}