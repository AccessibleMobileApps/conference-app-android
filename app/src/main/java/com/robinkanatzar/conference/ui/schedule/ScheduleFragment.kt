package com.robinkanatzar.conference.ui.schedule

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.*
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.get
import kotlinx.android.synthetic.main.schedule_fragment.*
import timber.log.Timber

class ScheduleFragment : Fragment(), Injectable, ScheduleAdapter.OnEventSelectedListener {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    lateinit var adapter: ScheduleAdapter
    private var conferenceId: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.schedule_fragment, null)

        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }

        val safeArgs: ScheduleFragmentArgs by navArgs()
        conferenceId = safeArgs.conferenceId

        if (this.conferenceId == null) {
            conferenceId = prefs[Constants.SP_CONFERENCE_ID]
            if (this.conferenceId == null) {
                val action = ScheduleFragmentDirections.chooseConferenceAction()
                findNavController().navigate(action)
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        query = firestore.collection(Constants.FB_SCHEDULE)
                .orderBy(Constants.FB_ORDER)
                .orderBy(Constants.FB_DATE_START, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.FB_CONFERENCE_ID, conferenceId)
                .limit(20)

        adapter = object : ScheduleAdapter(query, this@ScheduleFragment) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    rv_schedule.visibility = View.GONE
                } else {
                    rv_schedule.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Timber.i("onError, e = " + e.localizedMessage)
            }
        }

        rv_schedule.layoutManager = LinearLayoutManager(this.context)
        rv_schedule.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onEventSelected(event: DocumentSnapshot) {
        val action = ScheduleFragmentDirections.nextAction(event.id)
        findNavController().navigate(action)
    }
}