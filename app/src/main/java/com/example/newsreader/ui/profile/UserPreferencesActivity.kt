package com.example.newsreader.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsreader.R
import com.example.newsreader.common.InputValidation
import com.example.newsreader.localdb.DatabaseHelper
import com.example.newsreader.model.PreferenceM
import kotlinx.android.synthetic.main.activity_user_preferences.*

class UserPreferencesActivity : AppCompatActivity() {

    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_preferences)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()
    }

    private fun initViews() {

        textInputLayoutPref
        textInputEditPrefVal

        btnAddPref
        btnCancelPref
    }

    private fun initListeners() {
        btnAddPref!!.setOnClickListener {
            // do something to save data
            savePreferencesToDB()
        }
        btnCancelPref!!.setOnClickListener {
            // do something to clear
            clearEdits()
            finish()
        }
    }

    private fun initObjects() {
        inputValidation = InputValidation(this)
        databaseHelper = DatabaseHelper(this, null, null, 1)
    }

    private fun savePreferencesToDB() {
        if (!inputValidation!!.isInputEditTextFilled(
                textInputEditPrefVal,
                textInputLayoutPref,
                getString(R.string.error_message_value)
            )
        ) {
            Toast.makeText(this, "Enter Preference Value", Toast.LENGTH_SHORT).show()
            textInputEditPrefVal.requestFocus()
            return
        } else {
            val prefVal = PreferenceM()
            prefVal.pref_val = textInputEditPrefVal.text.toString()
            ProfileFragment.dbHandler.addPreference(this, prefVal)
            clearEdits()
            textInputEditPrefVal.requestFocus()
            //
        }
    }

    private fun clearEdits() {
        textInputEditPrefVal.text!!.clear()
    }
}
