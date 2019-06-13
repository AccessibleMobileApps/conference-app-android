package com.robinkanatzar.conference.ui.news

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
import kotlinx.android.synthetic.main.news_fragment.*
import timber.log.Timber

class NewsFragment : Fragment(), Injectable, NewsAdapter.OnArticleSelectedListener {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    lateinit var adapter: NewsAdapter
    private var conferenceId: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater,
                     container: ViewGroup?,
                     savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.news_fragment, null)

        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }
        conferenceId = prefs[Constants.SP_CONFERENCE_ID]
        if (this.conferenceId == null) {
            val action = NewsFragmentDirections.chooseConferenceAction()
            findNavController().navigate(action)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        query = firestore.collection(Constants.FB_NEWS)
                .orderBy(Constants.FB_ORDER, Query.Direction.ASCENDING)
                .whereEqualTo(Constants.FB_CONFERENCE_ID, conferenceId)
                .limit(20)

        adapter = object : NewsAdapter(query, this@NewsFragment) {
            override fun onDataChanged() {
                if (itemCount == 0) {
                    rv_news_articles.visibility = View.GONE
                } else {
                    rv_news_articles.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Timber.i("onError, e = " + e.localizedMessage)
            }
        }

        rv_news_articles.layoutManager = LinearLayoutManager(this.context)
        rv_news_articles.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onArticleSelected(article: DocumentSnapshot) {
        val action = NewsFragmentDirections.nextAction(article.id)
        findNavController().navigate(action)
    }
}