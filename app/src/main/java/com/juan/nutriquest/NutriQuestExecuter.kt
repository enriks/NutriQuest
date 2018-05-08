package com.juan.nutriquest

import android.content.Context

class NutriQuestExecuter{
    companion object {
        fun getQuestion(ct: Context, idPregunta: Pregunta){
            try {
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT * FROM Preguntas"
                val cursor = db.rawQuery(sql, null)
                if(cursor.isAfterLast){
                    cursor.getString(cursor.getColumnIndex(""))
                }
            }
            catch (e:Exception){

            }
        }

        fun guardarRespuesta(ct: Context, idPregunta: Int){
            try {
                val db = NutriQuestDB(ct).writableDatabase
                val sql = "INSERT INTO Respuestas"
                val cursor = db.rawQuery(sql, null)
                if(cursor.isAfterLast){
                    cursor.getString(cursor.getColumnIndex(""))
                }
            }
            catch (e:Exception){

            }
        }
    }
}