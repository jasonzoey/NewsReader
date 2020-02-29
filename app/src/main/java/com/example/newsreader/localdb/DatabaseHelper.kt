package com.example.newsreader.localdb

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import com.example.newsreader.model.PreferenceM

class DatabaseHelper(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // create table sql query
    private val CREATE_PREF_TABLE = ("CREATE TABLE " + TABLE_PREF + "("
            + COLUMN_PREF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_PREF_VAL + " TEXT" + ")")

    // drop table sql query
    private val DROP_PREF_TABLE = "DROP TABLE IF EXISTS $TABLE_PREF"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_PREF_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //Drop User Table if exist
        db.execSQL(DROP_PREF_TABLE)

        // Create tables again
        onCreate(db)

    }

    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    fun getAllPrefernces(cntxt: Context): ArrayList<PreferenceM> {

        val qry = "Select * From $TABLE_PREF"
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(qry,null)
        // array of columns to fetch
//        val columns = arrayOf(COLUMN_PREF_ID, COLUMN_PREF_VAL)
        // sorting orders
//        val sortOrder = "$COLUMN_PREF_VAL ASC"
        val preferencesList = ArrayList<PreferenceM>()
        if (cursor.count == 0){
            Toast.makeText(cntxt,"No Preference Records found",Toast.LENGTH_SHORT).show()
        }else{
            cursor.moveToFirst()
            while (!cursor.isAfterLast){
                val pref = PreferenceM()
                pref.pref_id = cursor.getInt(cursor.getColumnIndex(COLUMN_PREF_ID))
                pref.pref_val = cursor.getString(cursor.getColumnIndex(COLUMN_PREF_VAL))
                preferencesList.add(pref)
                cursor.moveToNext()
            }
            Toast.makeText(cntxt,"Preference Records found",Toast.LENGTH_SHORT).show()
        }
        cursor.close()
        db.close()
        return preferencesList
        // query the user table
//        val cursor = db.query(
//            TABLE_PREF, //Table to query
//            columns,            //columns to return
//            null,     //columns for the WHERE clause
//            null,  //The values for the WHERE clause
//            null,      //group the rows
//            null,       //filter by row groups
//            sortOrder)         //The sort order
//        if (cursor.moveToFirst()) {
//            Toast.makeText(cntxt,"No Preference Records found",Toast.LENGTH_SHORT).show()
//            do {
//                val pref = PreferenceM()
//                pref.pref_id = cursor.getString(cursor.getColumnIndex(COLUMN_PREF_ID)).toInt()
//                pref.pref_val = cursor.getString(cursor.getColumnIndex(COLUMN_PREF_VAL))
//                preferencesList.add(pref)
//            } while (cursor.moveToNext())
//            Toast.makeText(cntxt,"Preference Records found",Toast.LENGTH_SHORT).show()
//        }

    }


    /**
     * This method is to create preference record
     *
     * @param pref
     */
    fun addPreference(contextM: Context, pref: PreferenceM) {

        val values = ContentValues()
        values.put(COLUMN_PREF_VAL, pref.pref_val)
        val db = this.writableDatabase

        // Inserting Row
        try {
            db.insert(TABLE_PREF, null, values)
            Toast.makeText(contextM,"Preference Record Added",Toast.LENGTH_SHORT).show()

        }catch (e: Exception){
            Log.e(ContentValues.TAG, "error_db_insert")
        }
        db.close()
    }

    /**
     * This method is to delete preference record
     *
     * @param prefId
     */
    fun deletePref(prefId: Int): Boolean {

        val db = this.writableDatabase
        // delete user record by id
        val dltQry = "Delete From $TABLE_PREF where $COLUMN_PREF_ID = $prefId"
        var results: Boolean = false
        try {
            val cursor: Unit = db.execSQL(dltQry)
            results = true
        }catch (e: Exception){
            Log.e(ContentValues.TAG, "error_db_delete")
        }
        db.close()
        return  results
    }

    companion object {

        // Database Version
        private val DATABASE_VERSION = 1

        // Database Name
        private val DATABASE_NAME = "PreferenceManager.db"

        // User table name
        private val TABLE_PREF= "preferences"

        // User Table Columns names
        private val COLUMN_PREF_ID = "pref_id"
        private val COLUMN_PREF_VAL = "pref_val"
    }
}