package com.example.newsreader.common

import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.preference_item.view.*


class PreferenceViewHolder(private val view: View) : RecyclerView.ViewHolder(view){


    val cardView: CardView by lazy { view.pref_card_view }
    val prefVal: TextView by lazy { view.txt_pref_val}
    val prefDltBtn: ImageButton by lazy { view.img_btn_pref_dlt}
}