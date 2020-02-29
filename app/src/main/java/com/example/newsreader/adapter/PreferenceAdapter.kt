package com.example.newsreader.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.newsreader.R
import com.example.newsreader.common.PreferenceViewHolder
import com.example.newsreader.model.PreferenceM
import com.example.newsreader.ui.profile.ProfileFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PreferenceAdapter(

private val context: Context,
private var preferenceList: ArrayList<PreferenceM>
) : RecyclerView.Adapter<PreferenceViewHolder>() {

    private lateinit var viewGroupContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreferenceViewHolder {
        viewGroupContext = parent.context
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.preference_item, parent, false)
        return PreferenceViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return preferenceList.size
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder, position: Int) {
        val preferenceM: PreferenceM = preferenceList[position]
        holder.prefVal.text = preferenceM?.pref_val
        //
        holder.prefDltBtn.setOnClickListener {
            val selectedPref: String = preferenceM?.pref_val
            var aleartDlg: AlertDialog? = MaterialAlertDialogBuilder(context)
                .setTitle("Warning !")
                .setMessage("Delete Preference : $selectedPref ?")
                .setPositiveButton("Yes") { dialog, which ->
                    if (ProfileFragment.dbHandler.deletePref(preferenceM.pref_id)){
                        preferenceList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position,preferenceList.size)
                        Toast.makeText(context,"Preference Removed", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context,"Preference is nor Removed",Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("No") { dialog, which ->}
                .setIcon(R.drawable.ic_report_problem_black_24dp)
                .show()
        }
    }

}