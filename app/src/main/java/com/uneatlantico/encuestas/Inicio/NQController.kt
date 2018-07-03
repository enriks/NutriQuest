package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.uneatlantico.encuestas.DB.*
import com.uneatlantico.encuestas.WSReceiver.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject

/**
 * 2 opciones
 */
class NQController{

    val exq:NutriQuestExecuter

    val idPreguntas:ArrayList<Int> = ArrayList()
    val preguntas = ArrayList<Pregunta>()
    val preguntias = HashMap<Int, Pregunta>()
    //val preguntas = hashMapOf<Int, Pregunta>()
    var numeroPreguntas = 0
    var posicionPregunta = -1
    var numeroPregunta = 0
    var idEncuesta = 1
    val ct:Context
    val idUsuario:String
    private var clave = ""

    constructor(ct: Context, idEncuesta: Int){
        this.ct = ct
        exq = NutriQuestExecuter(ct)
        idUsuario = NutriQuestExecuter.idUsuario(ct)
        this.idEncuesta = idEncuesta

    }

    fun inicioEncuesta(idEncuesta: Int){
        //this.idEncuesta = idEncuesta
        //if(exq.getEncuesta(idEncuesta).numeroPreguntas == 0)
              primeraConexion(idEncuesta)//comenzarEncuesta(idEncuesta)
        /*else
            numeroPreguntas = exq.numeroPreguntas()*/

    }

    fun primeraConexion(idEncuesta: Int):Int{
        val datos = firstConexion2(ct, idEncuesta)
        if(datos == "-1")
            return -1
        val gson = Gson()
        Log.d("todo",  datos)
        val json = JSONObject(datos)
        val preguntaTotal = json.getJSONObject("pregunta")
        val preguntaRaw = preguntaTotal.getJSONObject("pregunta")
        val respuestasRaw = preguntaTotal.getJSONArray("respuestas")
        val clave = json.getString("clave")
        Log.d("clave", clave)
        val respuestas = ArrayList<Respuesta>()
        for (i in 0 until respuestasRaw.length()) {
            val respuesta = gson.fromJson<Respuesta>(respuestasRaw[i].toString(), Respuesta::class.java)
            respuesta.visibilidad = 1
            respuestas.add(respuesta)
        }
        numeroPreguntas = json.getString("numeroPreguntas").toInt()
        //preguntaRaw.put("respuestas", respuestas)
        //Log.d("preguntitatita", preguntaRaw.toString())
        val pregunta = gson.fromJson<Pregunta>(preguntaRaw.toString(), Pregunta::class.java)
        pregunta.posiblesRespuestas = respuestas
        if(pregunta.maxRespuestas == 0) {
            pregunta.maxRespuestas = respuestas.size
        }
        val idPregunta = pregunta._id
        //preguntas.add(pregunta)
        posicionPregunta = 0
        idPreguntas.add(idPregunta)
        preguntias.put(idPregunta, pregunta)
        /*exq.openRDB()
        if(exq.getClave(idEncuesta) == "") {
            exq.openWDB()
            exq.setEncuesta(EncuestaRaw(idEncuesta, idPregunta, numeroPreguntas, idPregunta, clave))
        }
        exq.closeDB()*/
        this.clave = clave
        return 0
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

        //exq.setEncuesta(EncuestaRaw(idEncuesta, idPregunta, numeroPreguntas, idPregunta, ""))
        //Log.d("numeroZ", numeroPreguntas.toString())
        doAsync {
            val encuestaBuilder = EncuestaBuilder(ct, idPreguntaSig, numeroPreguntas)
        }
    }

    fun recibirPreguntaX(idPregunta: Int):Int{

        exq.openRDB()
        try {
            val nQ = nQS(idPregunta, exq.idUsuario())
            if (preguntias.containsKey(nQ.toInt())) {
                Log.d("preguntasDisponibles", "idPreguntas: ${idPreguntas}")
                posicionPregunta = idPreguntas.indexOf(nQ.toInt())
                Log.d("preguntaDisponible2", "idPregunta: $nQ, posicionPregunta: $posicionPregunta")
                exq.closeDB(); return 0
            }
        }catch (e:Exception){Log.d("RecibirPReguntaXExp", e.message)}
        val preguntaTemp = getPregunta( idEncuesta, clave)
        exq.closeDB()

        if(preguntaTemp == "-1")
            return -1
        val gson = Gson()

        val preguntaTotal = JSONObject(preguntaTemp)
        val preguntaJson = preguntaTotal.getJSONObject("pregunta")

        val respuestasJson = preguntaTotal.getJSONArray("respuestas")
        val respuestas = ArrayList<Respuesta>()
        for(i in 0 until respuestasJson.length()){
            val respuesta = gson.fromJson<Respuesta>(respuestasJson[i].toString(), Respuesta::class.java)
            if(respuesta.visibilidad == null)
                respuesta.visibilidad = 1
            if(respuesta.categoriaVisibilidad == null)
                respuesta.categoriaVisibilidad = 1
            if(respuesta.contestadoAnterior == null)
                respuesta.contestadoAnterior = 0
            if(respuesta.determinaCategoria == null)
                respuesta.determinaCategoria = 0
            //Log.d("determinoCategoria", respuesta.determinaCategoria.toString())
            respuestas.add(respuesta)

        }
        //preguntaJson.(respuestas)
        Log.d("jsonPregunta", preguntaJson.toString())
        val pregunta = gson.fromJson<Pregunta>(preguntaJson.toString(), Pregunta::class.java)
        pregunta.posiblesRespuestas = respuestas
        if(pregunta.maxRespuestas == 0) {
            pregunta.maxRespuestas = respuestas.size
        }
        //Log.d("pregunta", )


        preguntias.put(pregunta._id, pregunta)
        idPreguntas.add(pregunta._id)
        posicionPregunta = idPreguntas.size-1
        //preguntas.add(posicionPregunta, pregunta)//gson.fromJson<Pregunta>(preguntaJson.toString(), Pregunta::class.java))
        //Log.d("idPreguntax", preguntias.toString())
        //Log.d("outOFnothing", "posicion: $posicionPregunta, tamaño: ${idPreguntas.size}")
        //Log.d("siguientePregunta", preguntas[posicionPregunta].pregunta)

        return 0
    }

    /**
     * determina cual sera la siguiente pregunta
     */
    fun nextQuestion(idPregunta: Int):Int {
        exq.openRDB()
        var preguntaSiguiente = idPregunta
        do {
            preguntaSiguiente = preguntaSiguiente(preguntaSiguiente)
            numeroPregunta++
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
    fun manejarRespuestas(idPreguntaPrevia:Int, idPregunta:Int, respuestas:ArrayList<Respuesta>):Int{
        //exq.openWDB()
        val respuestasX = ArrayList<RespuestaRaw>()
        respuestas.forEach {
            Log.d("respuestasEnvIdPre", idPregunta.toString())
            if(it.contestado == 1) {
                //respuestas[i]._id}, $idPregunta, ${respuestas[i].determinaCategoria}, $idPreguntaPrevia, ${respuestas[i].idPreguntaSiguiente}, ${respuestas[i].contestado}
                val dC = if(it.determinaCategoria == null) 0 else it.determinaCategoria
                respuestasX.add(RespuestaRaw(it._id, idPregunta, dC!!, idPreguntaPrevia, it.idPreguntaSiguiente, it.contestado))
                val respuestas = preguntias[idPregunta]!!.posiblesRespuestas
                preguntias[idPregunta]!!.posiblesRespuestas[respuestas.indexOf(it)].contestadoAnterior = 1
                preguntias[idPregunta]!!.posiblesRespuestas[respuestas.indexOf(it)].contestado = 0
            }
        }

        //inserto las respuestas a la pregunta en db movil
        /*exq.insertRespuestas(respuestasX)
        exq.updateRespuestaEncuesta(idPregunta, idEncuesta)
        exq.closeDB()*/
        //mando la respuesta a una pregunta a el ws
        val respuesta = if(sendUserResponses(respuestasX, idUsuario) == "1") 1 else 0
        return respuesta
    }

    fun ultimaPregunta():Int{
        val idPregunta = exq.getProgreso(idEncuesta)
        return idPregunta
    }

    companion object {
        /**
         * guardo el usuario logueado en DB y en la web
         */
        fun guardarUsuario(ct: Context, usuario:List<String>):Int{
            var enviado = 0
                if(enviarUsuario(usuario) == "1") {
                    NutriQuestExecuter.insertarUsuario(ct, usuario)
                    enviado = 1
                }
            return enviado
        }
    }
}