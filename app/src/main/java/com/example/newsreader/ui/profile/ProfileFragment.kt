package com.example.newsreader.ui.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsreader.R
import com.example.newsreader.adapter.PreferenceAdapter
import com.example.newsreader.localdb.DatabaseHelper
import com.example.newsreader.model.PreferenceM
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    var prefs: SharedPreferences? = null
    private lateinit var prefAdapter: PreferenceAdapter
    private lateinit var prefList: ArrayList<PreferenceM>

    companion object {
        lateinit var dbHandler: DatabaseHelper
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHandler = DatabaseHelper(activity!!.applicationContext, null, null, 1)
        // initializing the views
        initViews()
        // initializing the objects
        initObjects()
        // initializing the listeners
        initListeners()
    }

    private fun initViews() {

        textViewName
        setUserRegistered()
        addPreferencesFab

        viewPreferences()
    }
    override fun onStart() {
        super.onStart()
        viewPreferences()
    }
    private fun setUserRegistered() {
        val str_name: String = (prefs?.getString("name", "")).toString()
        textViewName.text = str_name
    }

    private fun viewPreferences() {
        prefList = dbHandler.getAllPrefernces(activity!!.applicationContext)
        prefAdapter = PreferenceAdapter(context!!, prefList)

        val rvPrefList: RecyclerView = rvUserPreferences
        rvPrefList.setHasFixedSize(true)
        rvPrefList.layoutManager = LinearLayoutManager(context)
        rvPrefList.itemAnimator = DefaultItemAnimator()
        rvPrefList.adapter = prefAdapter
    }

    private fun initListeners() {
        addPreferencesFab!!.setOnClickListener {
            //open the preferences add activity
            val intent = Intent(context, UserPreferencesActivity::class.java)
            context!!.startActivity(intent)
        }
    }

    private fun initObjects() {
        val mLayoutManager = LinearLayoutManager(activity!!.applicationContext)
    }

    override fun onResume() {
        super.onResume()
    }
}