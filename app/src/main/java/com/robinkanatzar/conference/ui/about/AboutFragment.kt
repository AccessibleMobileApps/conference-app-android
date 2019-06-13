package com.robinkanatzar.conference.ui.about

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.di.Injectable
import com.robinkanatzar.conference.util.Constants
import com.robinkanatzar.conference.util.MyWebViewClient
import com.robinkanatzar.conference.util.PreferenceHelper
import com.robinkanatzar.conference.util.PreferenceHelper.get
import com.robinkanatzar.conference.vo.About
import kotlinx.android.synthetic.main.about_fragment.*

class AboutFragment : Fragment(), Injectable, EventListener<DocumentSnapshot> {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private lateinit var aboutRef: DocumentReference
    private var aboutRegistration: ListenerRegistration? = null

    private var conferenceId: String? = null
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        context?.let { prefs = PreferenceHelper.defaultPrefs(it) }
        conferenceId = prefs[Constants.SP_CONFERENCE_ID]
        if (this.conferenceId == null) {
            val action = AboutFragmentDirections.chooseConferenceAction()
            findNavController().navigate(action)
        }

        aboutRef = firestore.collection(Constants.FB_ABOUT).document(conferenceId.toString())

        return inflater.inflate(R.layout.about_fragment, null)
    }

    override fun onStart() {
        super.onStart()
        aboutRef.let { aboutRegistration = aboutRef.addSnapshotListener(this) }
    }

    override fun onStop() {
        super.onStop()
        aboutRegistration?.remove()
        aboutRegistration = null
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            return
        }

        snapshot?.let {
            val about = snapshot.toObject(About::class.java)
            if (about != null) {
                onAboutLoaded(about)
            }
        }
    }

    private fun onAboutLoaded(about: About) {

        if (about.content_type.equals(About.TYPE_HTML)) {
            wv_about.visibility = View.VISIBLE
            sv_about.visibility = View.GONE

            wv_about.webViewClient = MyWebViewClient(this)

            about.url?.let {
                wv_about.loadUrl(about.url.toString())
            } ?: run {
                wv_about.loadData(about.content, "text/html; charset=utf-8", "UTF-8")
            }
        } else {
            wv_about.visibility = View.GONE
            sv_about.visibility = View.VISIBLE

            tv_about_title.text = about.title
            tv_about_content.text = about.content

            if (about.logo == null) {
                iv_about_image.visibility = View.GONE
            } else {
                iv_about_image.visibility = View.VISIBLE
                Glide.with(iv_about_image.context)
                        .load(about.logo)
                        .into(iv_about_image)
            }
        }
    }
}