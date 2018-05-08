package com.juan.nutriquest

import android.content.Context
import android.util.Log

class NutriQuestExecuter{
    companion object {

        /**
         * TODO como completar la query
         * necesito todos las posibles respuestas de la tabla RespuestasPosibles con el idPregunta del parametro
         */
        fun getQuestion(ct: Context, idPregunta: Int):Pregunta{
            var preguntaE = Pregunta()
            try {
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT pregunta FROM Preguntas WHERE _id = $idPregunta UNION SELECT respuesta FROM RespuestasPosibles where idPregunta = $idPregunta order by pregunta desc"
                val sql2 = "select r.pregunta pregunta,e.respuesta respuesta from Preguntas r, RespuestasPosibles e where r._id = $idPregunta ORDER BY r._id desc"
                var pregunta:String
                var respuestasPosibles: ArrayList<String> = ArrayList()
                val cursor = db.rawQuery(sql, null)
                var i = 0
                if(cursor.moveToFirst()){
                    pregunta = cursor.getString(cursor.getColumnIndex("pregunta"))
                    cursor.moveToNext()
                    while (!cursor.isAfterLast) {
                        respuestasPosibles.add(i, cursor.getString(cursor.getColumnIndex("pregunta")))
                        i++
                        cursor.moveToNext()
                    }
                    preguntaE = Pregunta(idPregunta, pregunta,posiblesRespuestas = respuestasPosibles )
                }
                cursor.close()
                db.close()
            }
            catch (e:Exception){
                Log.d("GetQuestionException", e.message)
            }
            return preguntaE
        }

        fun guardarRespuesta(ct: Context, idPregunta: Int, respuestas:List<String>){
            try {
                val db = NutriQuestDB(ct).writableDatabase
                var sql = "INSERT INTO respuestas (respuesta, idPregunta) VALUES "
                for(i in 1..respuestas.size){
                    val valuestemp = "('${respuestas[i]}', $idPregunta),"
                    sql += valuestemp
                }
                db.execSQL(sql)
                db.close()
            }
            catch (e:Exception){

            }
        }

        fun numeroPreguntas(ct: Context):Int{
            var numeroPreguntas:Int = 0
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT COUNT(_id) c FROM Preguntas"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    numeroPreguntas = cursor.getInt(cursor.getColumnIndex("c"))
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("numeroPreguntasExcepcion", e.message)}
            return numeroPreguntas
        }

        fun devolverRespuestasPilar(ct: Context):ArrayList<Pregunta>{
            var preguntas:ArrayList<Pregunta> = ArrayList()
            try {
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT idPregunta, respuesta FROM respuestas WHERE idPregunta < 5 order by _id desc"
                var idPregunta = 0
                var respuestasPosibles: ArrayList<String> = ArrayList()
                val cursor = db.rawQuery(sql, null)
                var i = 0
                if(cursor.moveToFirst()){

                    while (!cursor.isAfterLast) {

                        if(idPregunta != cursor.getInt(cursor.getColumnIndex("idPregunta"))) {
                            preguntas.add(Pregunta(id = idPregunta, posiblesRespuestas = respuestasPosibles))
                            respuestasPosibles.clear()
                            i = 0
                        }

                        idPregunta = cursor.getInt(cursor.getColumnIndex("idPregunta"))
                        respuestasPosibles.add(i, cursor.getString(cursor.getColumnIndex("respuesta")))

                        cursor.moveToNext()
                        i++
                    }
                    preguntas.add( Pregunta(id= idPregunta,posiblesRespuestas = respuestasPosibles))
                }
            }
            catch (e:Exception){
                Log.d("GetQuestionException", e.message)
            }
            return preguntas
        }

        fun numeroPreguntaActual(ct: Context):Int{
            var idPregunta:Int = 0
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT idPregunta FROM respuestas ORDER BY _id desc LIMIT 1"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    idPregunta = cursor.getInt(cursor.getColumnIndex("idPregunta"))
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("numeroPreguntasExcepcion", e.message)}
            return idPregunta
        }
    }
}