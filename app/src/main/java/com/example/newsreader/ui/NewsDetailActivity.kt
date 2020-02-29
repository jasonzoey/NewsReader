package com.example.newsreader.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.newsreader.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_news_detrail.*

class NewsDetailActivity : AppCompatActivity() {

    private val placeHolderImage = "https://picsum.photos/200/200/?blur"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detrail)

        val bundle = intent.extras
        val urlToImage = bundle?.get("url")
        val title = bundle?.get("title")
        val description = bundle?.get("desc")

        // handle passed data
        txt_title_detail.text = title?.toString()!!
        txt_details.text = description?.toString()!!
        checkForImageUrl(urlToImage.toString())
    }

    private fun checkForImageUrl(url: String) {
        if (url == null || url.isEmpty()) {
            Picasso.get()
                .load(placeHolderImage)
                .centerCrop()
                .fit().into(img_detail)
        } else {
            Picasso.get()
                .load(url)
                .centerCrop()
                .fit()
                .into(img_detail)
        }
    }
}

