package com.example.newsreader.adapter

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.newsreader.R
import com.example.newsreader.common.ArticleViewHolder
import com.example.newsreader.model.Article
import com.example.newsreader.ui.NewsDetailActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.article_item.view.*
import retrofit2.http.Url

//
class ArticleAdapter(

    private val context: Context,
    private var articleList: ArrayList<Article>
) : RecyclerView.Adapter<ArticleViewHolder>() {

    private val placeHolderImage = "https://picsum.photos/200/200/?blur"
    private lateinit var viewGroupContext: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): ArticleViewHolder {
        viewGroupContext = viewGroup.context
        val itemView: View =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.article_item, viewGroup, false)
        return ArticleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return articleList.size
    }

    override fun onBindViewHolder(articleViewHolder: ArticleViewHolder, itemIndex: Int) {
        val article: Article = articleList.get(itemIndex)
        setPropertiesForArticleViewHolder(articleViewHolder, article)
        articleViewHolder.cardView.setOnClickListener {
            //do something
            val urlString = article.urlToImage
            val titleString = article.title
            val descString = article.description

            val toPass = Bundle()
            toPass.putString("url", urlString)
            toPass.putString("title", titleString)
            toPass.putString("desc", descString)

            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtras(toPass)
            context.startActivity(intent)
        }
    }

    private fun setPropertiesForArticleViewHolder(
        articleViewHolder: ArticleViewHolder,
        article: Article
    ) {
        checkForUrlToImage(article, articleViewHolder)
        articleViewHolder.title.text = article?.title
        articleViewHolder.description.text = article?.description
        articleViewHolder.url.text = article?.url
    }

    private fun checkForUrlToImage(article: Article, articleViewHolder: ArticleViewHolder) {
        if (article.urlToImage == null || article.urlToImage.isEmpty()) {
            Picasso.get()
                .load(placeHolderImage)
                .centerCrop()
                .fit()
                .into(articleViewHolder.urlToImage)
        } else {
            Picasso.get()
                .load(article.urlToImage)
                .centerCrop()
                .fit()
                .into(articleViewHolder.urlToImage)
        }
    }

    fun setArticles(articles: ArrayList<Article>) {
        articleList = articles
        notifyDataSetChanged()
    }
}

