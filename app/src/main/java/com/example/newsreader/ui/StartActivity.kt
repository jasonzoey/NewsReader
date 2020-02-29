package com.example.newsreader.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.newsreader.MainActivity
import com.example.newsreader.R
import com.example.newsreader.common.InputValidation
import com.example.newsreader.localdb.DatabaseHelper
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.fragment_profile.*

class StartActivity : AppCompatActivity() {

    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper
    var prefs: SharedPreferences? = null
    private var PRIVATE_MODE = 0
    private val PREF_NAME = "REGISTER_DATA"
//    var prefs: SharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()
    }

    private fun initViews() {

        checkUserRegistered()
        textInputLayoutName
        textInputLayoutEmail

        textInputEditTextName
        textInputEditTextEmail

        buttonRegister
        addPreferencesFab
    }

    private fun initListeners() {
        buttonRegister!!.setOnClickListener {
            saveUserLocally()
        }
    }

    private fun initObjects() {
        inputValidation = InputValidation(this)
        databaseHelper = DatabaseHelper(this, null, null, 1)
    }

    private fun saveUserLocally() {
        if (!inputValidation!!.isInputEditTextFilled(
                textInputEditTextName,
                textInputLayoutName,
                getString(R.string.error_message_name)
            )
        ) {
            return
        }
        if (!inputValidation!!.isInputEditTextFilled(
                textInputEditTextEmail,
                textInputLayoutEmail,
                getString(R.string.error_message_email)
            )
        ) {
            return
        } else {
            prefs = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            val editor = prefs!!.edit()
            val string_name = textInputEditTextName.text.toString()
            val string_email = textInputEditTextEmail.text.toString()
            editor.putString("name", string_name)
            editor.putString("email", string_email)
            editor.apply()
            //  to show success message that record saved successfully
            Toast.makeText(this, getString(R.string.success_message), Toast.LENGTH_SHORT).show()
            emptyInputEditText()

            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }

    private fun checkUserRegistered() {
        val str_name: String = (prefs?.getString("name", "")).toString()
        val str_email: String = (prefs?.getString("email", "")).toString()
        if (str_name.isNotEmpty() || str_email.isNotEmpty()) {
            //hide the register view and show the preferences list view
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
    }
    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextName!!.text = null
        textInputEditTextEmail!!.text = null
    }
}
