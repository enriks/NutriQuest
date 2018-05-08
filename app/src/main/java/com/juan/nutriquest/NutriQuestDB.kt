package com.juan.nutriquest

import android.content.Context
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class NutriQuestDB(context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, "data/data/com.juan.nutriquest/databases/", null, DATABASE_VERSION) {
    companion object {

        private val DATABASE_NAME = "NutriQuestDB.db"
        private val DATABASE_VERSION = 1
    }

}