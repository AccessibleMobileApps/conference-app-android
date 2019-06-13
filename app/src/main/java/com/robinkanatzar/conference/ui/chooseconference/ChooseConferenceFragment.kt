package com.robinkanatzar.conference.ui.chooseconference

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.robinkanatzar.conference.MainActivity
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.set
import kotlinx.android.synthetic.main.choose_conference_layout.*
import timber.log.Timber
import com.google.firebase.firestore.*

class ChooseConferenceFragment : Fragment(), Injectable, ConferenceAdapter.OnConferenceSelectedListener {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    lateinit var adapter: ConferenceAdapter
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.robinkanatzar.conference.R.layout.choose_conference_layout, null)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }

        query = firestore.collection(Constants.FB_CONFERENCE)
                .orderBy(Constants.FB_ORDER)
                .orderBy(Constants.FB_NAME, Query.Direction.ASCENDING)
                .limit(20)

        adapter = object : ConferenceAdapter(query, this@ChooseConferenceFragment) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    rv_conferences.visibility = View.GONE
                } else {
                    rv_conferences.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Timber.i("onError, e = " + e.localizedMessage)
            }
        }

        rv_conferences.layoutManager = LinearLayoutManager(this.context)
        rv_conferences.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onConferenceSelected(conference: DocumentSnapshot, requires_login: Boolean) {
        if (requires_login) {
            val action = ChooseConferenceFragmentDirections.nextActionLogin(conference.id)
            findNavController().navigate(action)
        } else {
            prefs?.let { prefs[Constants.SP_CONFERENCE_ID] = conference.id }

            val activity = activity as MainActivity?
            activity?.let { it.setUpNavigationMenuHeader(conference.id) }

            val action = ChooseConferenceFragmentDirections.nextAction(conference.id)
            findNavController().navigate(action)
        }
    }
}