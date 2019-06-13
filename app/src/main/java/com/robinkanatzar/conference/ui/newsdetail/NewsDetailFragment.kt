package com.robinkanatzar.conference.ui.newsdetail

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
import com.robinkanatzar.conference.vo.Article
import kotlinx.android.synthetic.main.news_detail_fragment.*

class NewsDetailFragment : Fragment(), Injectable, EventListener<DocumentSnapshot> {

    lateinit var firestore: FirebaseFirestore
    lateinit var query: Query

    private lateinit var articleRef: DocumentReference
    private var articleRegistration: ListenerRegistration? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val safeArgs: NewsDetailFragmentArgs by navArgs()
        val newsArticleId = safeArgs.articleId

        if (BuildConfig.DEBUG) {
            FirebaseFirestore.setLoggingEnabled(true)
        }

        firestore = FirebaseFirestore.getInstance()

        articleRef = firestore.collection(Constants.FB_NEWS).document(newsArticleId)

        return inflater.inflate(R.layout.news_detail_fragment, null)
    }

    override fun onStart() {
        super.onStart()
        articleRegistration = articleRef.addSnapshotListener(this)
    }

    override fun onStop() {
        super.onStop()
        articleRegistration?.remove()
        articleRegistration = null
    }

    override fun onEvent(snapshot: DocumentSnapshot?, e: FirebaseFirestoreException?) {
        if (e != null) {
            return
        }

        snapshot?.let {
            val article = snapshot.toObject(Article::class.java)
            if (article != null) {
                onArticleLoaded(article)
            }
        }
    }

    private fun onArticleLoaded(article: Article) {

        if (article.content_type.equals(Article.TYPE_HTML)) {
            wv_news_detail.visibility = View.VISIBLE
            sv_news_detail.visibility = View.GONE

            wv_news_detail.webViewClient = MyWebViewClient(this)

            article.url?.let {
                wv_news_detail.loadUrl(article.url.toString())
            } ?: run {
                wv_news_detail.loadData(article.content, "text/html; charset=utf-8", "UTF-8")
            }
        } else {
            wv_news_detail.visibility = View.GONE
            sv_news_detail.visibility = View.VISIBLE
            tv_news_detail_content.text = article.content

            tv_news_detail_title.text = article.title

            if (article.logo == null) {
                iv_news_detail_item_image.visibility = View.GONE
            } else {
                iv_news_detail_item_image.visibility = View.VISIBLE
                Glide.with(iv_news_detail_item_image.context)
                        .load(article.logo)
                        .into(iv_news_detail_item_image)
            }
        }
    }
}