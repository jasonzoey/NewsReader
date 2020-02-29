package com.example.newsreader.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
//import com.example.newsreader.adapter.ItemClickListener
import kotlinx.android.synthetic.main.article_item.view.*

class ArticleViewHolder(private val view: View) : RecyclerView.ViewHolder(view){

    val cardView: CardView by lazy { view.card_view }
    val urlToImage: ImageView by lazy { view.article_urlToImage }
    val title: TextView by lazy { view.article_title }
    val description: TextView by lazy { view.article_description }
    val url: TextView by lazy { view.article_originalNewsUrl }
}