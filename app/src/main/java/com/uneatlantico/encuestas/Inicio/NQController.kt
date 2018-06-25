package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.uneatlantico.encuestas.DB.*
import com.uneatlantico.encuestas.WSReceiver.EncuestaBuilder
import com.uneatlantico.encuestas.WSReceiver.enviarUsuario
import com.uneatlantico.encuestas.WSReceiver.firstConexion
import com.uneatlantico.encuestas.WSReceiver.sendUserResponses
import org.jetbrains.anko.doAsync
import org.json.JSONObject

/**
 * 2 opciones
 */
class NQController{

    val exq:NutriQuestExecuter

    val idPreguntas:ArrayList<Int> = ArrayList()
    var numeroPreguntas = 0

    fun inicioEncuesta(idEncuesta: Int){
        if(exq.getEncuesta(idEncuesta).numeroPreguntas == 0)
              comenzarEncuesta(idEncuesta)

    }

    private fun comenzarEncuesta(idEncuesta: Int){
        val datos = firstConexion(ct, idEncuesta)

        Log.d("todo",  datos)
        val json = JSONObject(datos)
        val preguntaTotal = json.getJSONObject("pregunta")
        val preguntaRaw = preguntaTotal.getJSONObject("pregunta")
        val respuestasRaw = preguntaTotal.getJSONArray("respuestas")
//        val visibilidadesRaw = preguntaTotal.getJSONArray("visibilidades")
        val categoriasRaw = preguntaTotal.getJSONArray("categorias")
        val gson = Gson()

        val pregunta = gson.fromJson<PreguntaRaw>(preguntaRaw.toString(), PreguntaRaw::class.java)
        val respuestas = ArrayList<RespuestaPosibleRaw>()
        for (i in 0 until respuestasRaw.length()) {
            respuestas.add(gson.fromJson<RespuestaPosibleRaw>(respuestasRaw[i].toString(), RespuestaPosibleRaw::class.java))
        }

        /*val visibilidades = ArrayList<VisibilidadRaw>()
        for (i in 0 until visibilidadesRaw.length()) {
            visibilidades.add(gson.fromJson<VisibilidadRaw>(respuestasRaw[i].toString(), VisibilidadRaw::class.java))
        }*/

        val categorias = ArrayList<CategoriaRaw>()
        for (i in 0 until categoriasRaw.length()) {

            categorias.add(gson.fromJson<CategoriaRaw>(categoriasRaw[i].toString(), CategoriaRaw::class.java))
            //Log.d("categoria", categorias.get(i).toString())
        }

        exq.setPregunta(pregunta)
        exq.setRespuesta(respuestas)
        //exq.setVisibilidad(visibilidades)
        exq.setCategorias(categorias)
        val idPregunta = pregunta._id
        var idPreguntaSig = json.getString("idPreguntaSiguiente").toInt()
        val numeroPreguntas = json.getJSONObject("numeroPreguntas").getString("numeroPreguntas").toInt()
        this.numeroPreguntas = numeroPreguntas
        exq.setEncuesta(EncuestaRaw(idEncuesta, idPregunta, numeroPreguntas))
        //Log.d("numeroZ", numeroPreguntas.toString())
        doAsync {
            val encuestaBuilder = EncuestaBuilder(ct, idPreguntaSig, numeroPreguntas)
        }
    }

    val ct:Context

    constructor(ct: Context){
        this.ct = ct
        exq = NutriQuestExecuter(ct)
    }



    /**
     * determina cual sera la siguiente pregunta
     */
    fun nextQuestion(idPregunta: Int):Int {
        exq.openRDB()
        var preguntaSiguiente = idPregunta
        do {
            preguntaSiguiente = preguntaSiguiente(preguntaSiguiente)
            if(preguntaSiguiente == -1){
                break
            }
            //idPreguntas.add(preguntaSiguiente)
        } while (seguir(preguntaSiguiente))

        exq.closeDB()
        return preguntaSiguiente
    }

    /**
     * consigo el id de pregunta siguiente
     */
    fun preguntaSiguiente(idPregunta: Int):Int{
        var idPreguntaSiguiente = 0

        //ids[0] puntero de pregunta
        //ids[1] puntero de respuesta
        val ids = exq.numeroPreguntaSiguiente(ct, idPregunta)
        if(ids[1] == 0){
            return ids[0]
        }
        idPreguntaSiguiente = if(ids[0] != ids[1]) ids[1] else ids[0]
        return idPreguntaSiguiente
    }

    /**
     * Comprueba si se debe mostrar una pregunta por id al Usuario
     */
    private fun seguir(idPregunta: Int):Boolean{
        var seguir = false
        val categorias = exq.getCategoriasUsuario(ct)
        val pregunta = exq.getPregunta(ct, idPregunta)

        when(pregunta.visibilidad){
            // si la pregunta tiene visibilidad 1, significa que si categoria = pregunta.categoria paras, si no, sigues
            1 -> if(!categorias.contains(pregunta.idCategoria)) seguir = true

            // con pregunta.visibilidad = 2 si categoria = pregunta.categoria sigues, si no, continuas
            2 -> if(categorias.contains(pregunta.idCategoria)) seguir = true
        }
        /*if(seguir){
            insertarVacio(ct, idPregunta)
        }*/

        return seguir
    }

   // companion object {

    /**
     *
     * Creo la pregunta juntando preguntas, respuestas, y el numero de respuestas posibles a responder
     *
     * visibilidad 1 es que es visible
     * visibilidad 2 es que no lo es
     * visibilidad 0 es que no tiene un valor definido
     */
    fun formarPregunta(idPregunta: Int): Pregunta {

        var preguntaCompleta: Pregunta

        exq.openWDB()

        val pregunta = exq.getPregunta(ct, idPregunta)
        val respuestas = exq.getRespuestas(idPregunta)
        val categorias = exq.getCategoriasUsuario(ct)

        exq.closeDB()

        respuestas.forEach {
            when(it.visibilidad){
                1 -> if(!categorias.contains(it.categoriaVisibilidad)) it.visibilidad = 2

                2 -> if(categorias.contains(it.categoriaVisibilidad)) it.visibilidad = 2

                else -> it.visibilidad = 1
            }
        }

        //seteo la maxima cantidad de respuestas
        var max:Int = 1
        if(pregunta.maxRespuestas ==0)
            max =respuestas.size

        //lleno la pregunta
        preguntaCompleta = Pregunta(pregunta._id, pregunta = pregunta.pregunta, posiblesRespuestas = respuestas, maxRespuestas = max, minRespuestas = pregunta.minRespuestas)
        /*preguntaCompleta.posiblesRespuestas.forEach{
            //Log.d("pregunta",it.visibilidad.toString())
        }*/


        return preguntaCompleta
    }

    /**
     * envia todas las respuestas al server
     */
    /*fun mandarTodasLasRespuestas(ct:Context){
        doAsync {
            var respuestas = getAllRespuestas(ct)
            sendUserResponses(respuestas, ct)
        }
    }*/

    /**
     * Una vez que el usuario avance de pregunta guarda y envia las respuestas
     */
    fun manejarRespuestas(idPreguntaPrevia:Int, idPregunta:Int, respuestas:ArrayList<Respuesta>){
        exq.openWDB()
        val respuestasWS = ArrayList<RespuestasUsuario>()
        val respuestasDB = ArrayList<Respuesta>()
        respuestas.forEach {
            if(it.contestado == 1) {
                respuestasWS.add(RespuestasUsuario(it.respuesta, idPregunta, it.determinaCategoria, idPreguntaPrevia, it.idPreguntaSiguiente, it.contestado))
                respuestasDB.add(it)
            }
        }

        //inserto las respuestas a la pregunta en db movil
        exq.insertRespuestas(idPreguntaPrevia, idPregunta, respuestas = respuestasDB)

        exq.closeDB()
        //mando la respuesta a una pregunta a el ws
        sendUserResponses(respuestasWS, ct)

    }

    companion object {
        /**
         * guardo el usuario logueado en DB y en la web
         */
        fun guardarUsuario(ct: Context, usuario:List<String>){
            doAsync {
                NutriQuestExecuter.insertarUsuario(ct, usuario)
                enviarUsuario(ct, usuario)
            }
        }
    }
}