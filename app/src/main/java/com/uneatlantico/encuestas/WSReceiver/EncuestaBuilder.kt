package com.uneatlantico.encuestas.WSReceiver

import android.content.Context
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.google.gson.Gson
import com.uneatlantico.encuestas.DB.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject

class EncuestaBuilder {

    //val json = "{\"pregunta\":[{\"_id\":\"2\",\"pregunta\":\"Qué tomas realiza usted diariamente\",\"minRespuestas\":\"1\",\"maxRespuestas\":\"0\",\"idSiguientePregunta\":\"3\",\"idSeccion\":\"1\"}],\"respuestas\":[{\"_id\":\"4\",\"respuesta\":\"Desayuno\",\"idPregunta\":\"2\",\"idCategoria\":\"5\",\"idSiguientePregunta\":\"0\"},{\"_id\":\"5\",\"respuesta\":\"Media mañana\",\"idPregunta\":\"2\",\"idCategoria\":\"6\",\"idSiguientePregunta\":\"0\"}],\"visibilidad\":[{\"_id\":\"1\",\"idElemento\":\"8\",\"tipoElemento\":\"0\",\"idCategoria\":\"1\",\"visibilidad\":\"2\"},{\"_id\":\"2\",\"idElemento\":\"9\",\"tipoElemento\":\"0\",\"idCategoria\":\"1\",\"visibilidad\":\"2\"}],\"categorias\":[{\"_id\":\"1\",\"categoria\":\"no carne an\"}]}"

    val ct:Context
    val exq: NutriQuestExecuter
    var idPregunta:Int
    constructor(ct:Context, idPregunta:Int){
        this.ct = ct
        this.exq = NutriQuestExecuter(ct)
        this.idPregunta = idPregunta
        guardarEncuesta()
    }

    private fun guardarEncuesta() {
        try {
            exq.openWDB()
            while (idPregunta<34) {
                guardarPreguntaCompleta()
            }
            exq.closeDB()
        } catch (e:Exception){
            Log.d("guardarEncuestaExcp", e.toString())}
    }

    fun guardarPreguntaCompleta(){
        val preguntaTemp = recibirPregunta( idPregunta, ct )
        guardarPregunta(preguntaTemp)
    }

    private fun guardarPregunta(pregunta:String) {
        val gson = Gson()
        //Log.d("todoelJson", "nohay"+pregunta)

        val json = JSONObject(pregunta)


        var preguntasTemp = json.getJSONArray("pregunta")
        val preguntaS = gson.fromJson<PreguntaRaw>(preguntasTemp[0].toString(), PreguntaRaw::class.java)
        Log.d("pregunta", preguntaS.toString())

        if(exq.existePregunta(idPregunta))
            Log.d("idPregunta", idPregunta.toString())
        else {
            doAsync {
                guardarPregunta(preguntaS)
            }

            doAsync {
                var respuestas = ArrayList<RespuestaPosibleRaw>()
                val respuestasTemp = json.getJSONArray("respuestas")
                for (i in 0 until respuestasTemp.length()) {
                    val respuesta = gson.fromJson<RespuestaPosibleRaw>(respuestasTemp[i].toString(), RespuestaPosibleRaw::class.java)
                    respuestas.add(respuesta)
                }
                guardarRespuestas(respuestas)
            }

            doAsync {
                try {
                    var categoriasTemp = json.getJSONArray("categorias")
                    var categorias = ArrayList<CategoriaRaw>()
                    for (i in 0 until categoriasTemp.length()) {
                        val categoria = gson.fromJson<CategoriaRaw>(categoriasTemp[i].toString(), CategoriaRaw::class.java)
                        categorias.add(categoria)
                    }
                    guardarCategorias(categorias)
                } catch (po: Exception) {
                }
            }

            doAsync {
                try {
                    var visibilidadTemp = json.getJSONArray("visibilidades")
                    var visibilidades = ArrayList<VisibilidadRaw>()
                    for (i in 0 until visibilidadTemp.length()) {
                        val visibilidad = gson.fromJson<VisibilidadRaw>(visibilidadTemp[i].toString(), VisibilidadRaw::class.java)
                        visibilidades.add(visibilidad)
                    }
                    guardarVisibilidad(visibilidades)
                } catch (e: Exception) {
                }
            }
        }
        idPregunta = preguntaS.idPreguntaSiguiente
    }

    /**
     * guardar en DB pregunta Completa
     */
    fun guardarPregunta(pregunta:PreguntaRaw){

        /*val pregunta: PreguntaRaw
        pregunta = PreguntaRaw(pregunta)*/

        exq.setPregunta(pregunta)
    }

    fun guardarRespuestas(respuestas:ArrayList<RespuestaPosibleRaw>){

        if (!respuestas.isEmpty())
        exq.setRespuesta(respuestas)

    }

    fun guardarCategorias(categorias:ArrayList<CategoriaRaw>){

        if(!categorias.isEmpty())
            exq.setCategorias(categorias)
    }

    fun guardarVisibilidad(visibilidades:ArrayList<VisibilidadRaw>){
        if(!visibilidades.isEmpty())
            exq.setVisibilidad(visibilidades)
    }

    fun generarPregunta(){

    }
}