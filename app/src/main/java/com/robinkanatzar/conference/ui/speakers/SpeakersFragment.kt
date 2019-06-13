package com.robinkanatzar.conference.ui.speakers

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.speakers_fragment.*
import timber.log.Timber

class SpeakersFragment : Fragment(), Injectable, SpeakersAdapter.OnSpeakerSelectedListener {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    lateinit var adapter: SpeakersAdapter
    private var conferenceId: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.speakers_fragment, null)

        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }
        conferenceId = prefs[Constants.SP_CONFERENCE_ID]
        if (this.conferenceId == null) {
            val action = SpeakersFragmentDirections.chooseConferenceAction()
            findNavController().navigate(action)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        query = firestore.collection(Constants.FB_SPEAKERS)
                .orderBy(Constants.FB_ORDER, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.FB_CONFERENCE_ID, conferenceId)
                .limit(20)

        adapter = object : SpeakersAdapter(query, this@SpeakersFragment) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    rv_speakers.visibility = View.GONE
                } else {
                    rv_speakers.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Timber.i("onError, e = " + e.localizedMessage)
            }
        }

        rv_speakers.layoutManager = LinearLayoutManager(this.context)
        rv_speakers.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onSpeakerSelected(speaker: DocumentSnapshot) {
        val action = SpeakersFragmentDirections.nextAction(speaker.id)
        findNavController().navigate(action)
    }
}