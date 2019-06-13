package com.robinkanatzar.conference.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.robinkanatzar.conference.R
import com.robinkanatzar.conference.ui.common.FirestoreAdapter
import com.robinkanatzar.conference.vo.Article
import com.robinkanatzar.conference.vo.Article.Companion.TYPE_SHORT
import kotlinx.android.synthetic.main.news_item.view.*

open class NewsAdapter(query: Query, private val listener: OnArticleSelectedListener) :
        FirestoreAdapter<NewsAdapter.ViewHolder>(query) {

    interface OnArticleSelectedListener {
        fun onArticleSelected(article: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.news_item, parent, false))
    }

    override fun onBindViewHolder(holder: NewsAdapter.ViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                snapshot: DocumentSnapshot,
                listener: NewsAdapter.OnArticleSelectedListener?
        ) {
            val article = snapshot.toObject(Article::class.java)
            if (article == null) {
                return
            }

            itemView.tv_news_item_title.text = article.title

            if (article.logo == null) {
                itemView.iv_news_item_image.visibility = View.GONE
            } else {
                itemView.iv_news_item_image.visibility = View.VISIBLE
                Glide.with(itemView.iv_news_item_image.context)
                        .load(article.logo)
                        .into(itemView.iv_news_item_image)
            }

            if (article.type.equals(TYPE_SHORT)) {
                itemView.tv_news_item_content_short.visibility = View.VISIBLE
                itemView.iv_news_item_more_arrow.visibility = View.GONE
                itemView.tv_news_item_content_short.text = article.content
            } else {
                itemView.tv_news_item_content_short.visibility = View.GONE
                itemView.iv_news_item_more_arrow.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                if (!article.type.equals(TYPE_SHORT)) {
                    listener?.onArticleSelected(snapshot)
                }
            }
        }
    }
}