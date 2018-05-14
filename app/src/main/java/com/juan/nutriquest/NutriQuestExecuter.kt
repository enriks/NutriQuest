package com.juan.nutriquest

import android.content.Context
import android.util.Log

class NutriQuestExecuter{
    companion object {

        /**
         * TODO como completar la query
         * necesito todos las posibles respuestas de la tabla RespuestasPosibles con el idPregunta del parametro
         */
        /*fun getQuestion(ct: Context, idPregunta: Int):Pregunta{
            var preguntaE = Pregunta()
            try {
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT pregunta FROM Preguntas WHERE _id = $idPregunta UNION SELECT respuesta FROM RespuestasPosibles where idPregunta = $idPregunta order by pregunta desc"
                val sql2 = "select p.pregunta pregunta,r.respuesta respuesta from Preguntas p, RespuestasPosibles r where p._id = $idPregunta and r.idPregunta = $idPregunta ORDER BY p._id desc"
                val sql3 = "SELECT p.pregunta, c.idCategoria, c.visibilidad FROM Preguntas p, CategoriaElementoVisibilidad c WHERE p._id = 8 and c.idElemento = 8 and c.tipoElemento = 0 UNION SELECT r.respuesta, c.idCategoria, c.visibilidad FROM RespuestasPosibles r, CategoriaElementoVisibilidad c where r.idPregunta = 8 and r._id = c.idElemento and c.tipoElemento = 1 order by pregunta desc"
                val sql4 = "SELECT * from (select _id, pregunta FROM Preguntas WHERE _id = 8 UNION SELECT _id, respuesta FROM RespuestasPosibles where idPregunta = 8 order by pregunta) t1 left JOIN CategoriaElementoVisibilidad c ON c.idElemento = t1._id"
                val sql5 = "SELECT pregunta, idCategoria, visibilidad from (SELECT * from (select _id, pregunta FROM Preguntas WHERE _id = 8 UNION SELECT _id, respuesta FROM RespuestasPosibles where idPregunta = 8 ) t1  left JOIN CategoriaElementoVisibilidad c ON c.idElemento = t1._id)"
                val sql6=""
                var pregunta:String
                var respuestasPosibles: ArrayList<String> = ArrayList()
                val cursor = db.rawQuery(sql2, null)
                var i = 0
                if(cursor.moveToFirst()){
                    pregunta = cursor.getString(cursor.getColumnIndex("pregunta"))
                    while (!cursor.isAfterLast) {
                        respuestasPosibles.add(i, cursor.getString(cursor.getColumnIndex("respuesta")))
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
        }*/

        /**
         * devuelve una pregunta con sus limitaciones
         */
        fun getPregunta(ct: Context, idPregunta: Int):SoloPregunta{
            var pregunta = SoloPregunta()
            try {
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "select pregunta, idCategoria, visibilidad from (select p._id, pregunta from Preguntas p where _id = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 0"
                val cursor = db.rawQuery(sql, null)
                var respuesta:String
                var idCategoria:Int
                var visibilidad:Int
                if(cursor.moveToFirst()){
                    while (!cursor.isAfterLast) {
                        respuesta = cursor.getString(cursor.getColumnIndex("pregunta"))
                        idCategoria = cursor.getInt(cursor.getColumnIndex("idCategoria"))
                        visibilidad = cursor.getInt(cursor.getColumnIndex("visibilidad"))
                        pregunta = SoloPregunta(_id = idPregunta, pregunta = respuesta, idCategoria = idCategoria, visibilidad = visibilidad)
                        cursor.moveToNext()
                    }
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("getPreguntaException", e.message)}
            return pregunta
        }

        /**
         * devuelve todas las respuestas y sus limitaciones para una pregunta dada
         */
        fun getRespuestas(ct: Context, idPregunta: Int):ArrayList<Respuesta>{

            var respuestasPosibles: ArrayList<Respuesta> = ArrayList()
            try {
                val db = NutriQuestDB(ct).readableDatabase
                var pregunta:String
                val sql1 = "select respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad from (select r._id, respuesta from RespuestasPosibles r where idPregunta = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 1"
                val sql = "select respuesta, t.idCategoria determinaCategoria,c.idCategoria categoriaVisibilidad, visibilidad from (select r._id, respuesta, idCategoria from RespuestasPosibles r where idPregunta = $idPregunta) t left join CategoriaElementoVisibilidad c on idElemento = t._id and tipoElemento = 1"
                val cursor = db.rawQuery(sql, null)
                var respuesta:String
                var determinaCategoria:Int
                var categoriaVisibilidad:Int
                var visibilidad:Int
                if(cursor.moveToFirst()){
                    while (!cursor.isAfterLast) {
                        respuesta = cursor.getString(cursor.getColumnIndex("respuesta"))
                        determinaCategoria = cursor.getInt(cursor.getColumnIndex("determinaCategoria"))
                        categoriaVisibilidad = cursor.getInt(cursor.getColumnIndex("categoriaVisibilidad"))
                        visibilidad = cursor.getInt(cursor.getColumnIndex("visibilidad"))
                        //Log.d("micategoria", determinaCategoria.toString())
                        respuestasPosibles.add(Respuesta(respuesta = respuesta, categoriaVisibilidad= categoriaVisibilidad,determinaCategoria = determinaCategoria, visibilidad = visibilidad))
                        cursor.moveToNext()
                    }
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("getRespuestaException", e.message)}
            return respuestasPosibles
        }

        /**
         * devuelve todas las categorias asignadas al usuario por sus respuestas
         */
        fun getCategoriasUsuario(ct: Context):List<Int>{
            var categoriasUsuario:ArrayList<Int> = ArrayList()
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT idCategoria FROM Respuestas where contestado = 1 and idCategoria != 0"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    while (!cursor.isAfterLast) {
                        categoriasUsuario.add(cursor.getInt(cursor.getColumnIndex("idCategoria")))
                        cursor.moveToNext()
                    }
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("numeroPreguntasExcepcion", e.message)}
            return categoriasUsuario
        }

        fun guardarRespuesta(ct: Context, idPregunta: Int, respuestas:List<String>){
            try {
                val db = NutriQuestDB(ct).writableDatabase
                var sql = "INSERT INTO Respuestas (respuesta, idPregunta) VALUES "
                for(i in 0 until respuestas.size){
                    val valuestemp = "('${respuestas[i]}', $idPregunta)"
                    sql += valuestemp
                    if(i+1 < respuestas.size)
                        sql += ","
                }
                Log.d("insertstatement", sql)
                db.execSQL(sql)
                db.close()
            }
            catch (e:Exception){
                Log.d("excepcionInsertRespuesta", e.message)
            }
        }

        fun insertRespuestas(ct:Context, idPreguntaPrevia: Int, idPregunta: Int,idPreguntaPosterior: Int, respuestas:ArrayList<Respuesta>){
            val respuestass = respuestas as List<Respuesta>
            try {
                val db = NutriQuestDB(ct).writableDatabase
                var sql = "INSERT INTO Respuestas (respuesta, idPregunta, idCategoria, idPreguntaPrevia, idPreguntaPosterior, contestado) VALUES "
                for(i in 0 until respuestas.size){

                    val valuestemp = "('${respuestas[i].respuesta}', $idPregunta, ${respuestas[i].determinaCategoria}, $idPreguntaPrevia, $idPreguntaPosterior, ${respuestas[i].contestado})"
                    sql += valuestemp
                    if (i + 1 < respuestas.size)
                        sql += ","

                }
                Log.d("insertstatement", sql)
                db.execSQL(sql)
                db.close()
            }
            catch (e:Exception){
                Log.d("excepcionInsertRespuesta", e.message)
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

        /*fun devolverRespuestasPilar(ct: Context):ArrayList<Pregunta>{
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
        }*/

        fun numeroPreguntaSiguiente(ct: Context, idPregunta:Int):Int{
            var idPregunta2:Int = 0
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT idSiguientePregunta FROM Preguntas WHERE _id = $idPregunta"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    idPregunta2 = cursor.getInt(cursor.getColumnIndex("idSiguientePregunta"))
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("numeroPreguntasExcepcion", e.message)}
            return idPregunta2
        }


        fun ultimaPregunta(ct:Context):Int{
            var idPreguntaUltima:Int = 0
            try{
                val db = NutriQuestDB(ct).readableDatabase
                val sql = "SELECT _id FROM Preguntas WHERE idSiguientePregunta"
                val cursor = db.rawQuery(sql, null)
                if(cursor.moveToFirst()){
                    idPreguntaUltima = cursor.getInt(cursor.getColumnIndex("idSiguientePregunta"))
                }
                cursor.close()
                db.close()
            }catch (e:Exception){Log.d("numeroPreguntasExcepcion", e.message)}
            return idPreguntaUltima
        }

        fun deleteAll(ct: Context){
            val db = NutriQuestDB(ct).writableDatabase
            db.execSQL("DELETE FROM Respuestas")
            db.close()
        }

    }
}