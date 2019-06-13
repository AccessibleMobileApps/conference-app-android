package com.robinkanatzar.conference.ui.speakersdetail

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
import com.robinkanatzar.conference.vo.Speaker
import kotlinx.android.synthetic.main.speakers_detail_fragment.*

class SpeakersDetailFragment : Fragment(), Injectable, EventListener<DocumentSnapshot> {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private lateinit var speakerRef: DocumentReference
    private var speakerRegistration: ListenerRegistration? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val safeArgs: SpeakersDetailFragmentArgs by navArgs()
        val speakerId = safeArgs.speakerId

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        speakerRef = firestore.collection(Constants.FB_SPEAKERS).document(speakerId)

        return inflater.inflate(R.layout.speakers_detail_fragment, null)
    }

    override fun onStart() {
        super.onStart()
        speakerRegistration = speakerRef.addSnapshotListener(this)
    }

    override fun onStop() {
        super.onStop()
        speakerRegistration?.remove()
        speakerRegistration = null
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            return
        }

        snapshot?.let {
            val speaker = snapshot.toObject(Speaker::class.java)
            if (speaker != null) {
                onSpeakerLoaded(speaker)
            }
        }
    }

    private fun onSpeakerLoaded(speaker: Speaker) {
        tv_speakers_detail_title.text = getString(R.string.speakers_first_and_last_name, speaker.first_name, speaker.last_name)
        tv_speakers_detail_content.text = speaker.content
        tv_speakers_detail_tag_line.text = speaker.tag_line

        if (speaker.logo == null) {
            iv_speakers_detail_item_image.visibility = View.GONE
        } else {
            iv_speakers_detail_item_image.visibility = View.VISIBLE
            Glide.with(iv_speakers_detail_item_image.context)
                    .load(speaker.logo)
                    .into(iv_speakers_detail_item_image)
        }
    }

}