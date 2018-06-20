package com.uneatlantico.encuestas

import android.content.Context
import android.util.Log
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.getAllRespuestas
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.getCategoriasUsuario
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.getPregunta
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.getRespuestas
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.numeroPreguntaSiguiente
import org.jetbrains.anko.doAsync

/**
 * 2 opciones
 */
class NQController{

    //TODO array con todas las preguntas
    val idPreguntas:ArrayList<Int> = ArrayList()


    fun inicioEncuesta(ct: Context){
        firstConexion(ct)
    }

    /**
     * determina cual sera la siguiente pregunta
     */
    fun nextQuestion(ct:Context, idPregunta: Int):Int {

        var preguntaSiguiente = idPregunta
        do {
            preguntaSiguiente = preguntaSiguiente(ct, preguntaSiguiente)
            if(preguntaSiguiente == 0){
                preguntaSiguiente = -1
                break
            }
            //idPreguntas.add(preguntaSiguiente)
        } while (seguir(ct, preguntaSiguiente))

        return preguntaSiguiente
    }

    /**
     * Une las preguntas con las respuestas validas
     */
    private fun seguir(ct: Context, idPregunta: Int):Boolean{
        var seguir = false
        val categorias = getCategoriasUsuario(ct)
        val pregunta = getPregunta(ct, idPregunta)

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

    companion object {

        /**
         *
         * Creo la pregunta juntando preguntas, respuestas, y el numero de respuestas posibles a responder
         *
         * visibilidad 1 es que es visible
         * visibilidad 2 es que no lo es
         * visibilidad 0 es que no tiene un valor definido
         */
        fun formarPregunta(ct:Context, idPregunta: Int):Pregunta{
            var preguntaCompleta:Pregunta
            val categorias = getCategoriasUsuario(ct)
            val pregunta = getPregunta(ct, idPregunta)
            val respuestas = getRespuestas(ct, idPregunta)
            var max:Int = 1
            Log.d("categorias", categorias.toString())

            //seteo la visibilidad de cada respuesta
            respuestas.forEach {
                when(it.visibilidad){
                    1 -> if(!categorias.contains(it.categoriaVisibilidad)) it.visibilidad = 2

                    2 -> if(categorias.contains(it.categoriaVisibilidad)) it.visibilidad = 2

                    else -> it.visibilidad = 1
                }
            }

            //seteo la maxima cantidad de respuestas
            if(pregunta.maxRespuestas ==0)
                max =respuestas.size
            preguntaCompleta = Pregunta(pregunta._id, pregunta = pregunta.pregunta, posiblesRespuestas = respuestas, maxRespuestas = max, minRespuestas = pregunta.minRespuestas)
            preguntaCompleta.posiblesRespuestas.forEach{
                //Log.d("pregunta",it.visibilidad.toString())
            }
            return preguntaCompleta
        }

        fun mandarTodasLasRespuestas(ct:Context){
            doAsync {
                var respuestas = getAllRespuestas(ct)
                sendUserResponses(respuestas, ct)
            }
        }

        /**
         * Una vez que el usuario avance de pregunta guarda y envia las respuestas
         */
        fun manejarRespuestas(ct: Context,  idPreguntaPrevia:Int,idPregunta:Int, respuestas:ArrayList<Respuesta>){
            val respuesta = ArrayList<RespuestasUsuario>()
            val respuestas2 = ArrayList<Respuesta>()
            respuestas.forEach {
                if(it.contestado == 1) {
                    respuesta.add(RespuestasUsuario(it.respuesta, idPregunta, it.determinaCategoria, idPreguntaPrevia, it.idPreguntaSiguiente, it.contestado))
                    respuestas2.add(it)
                }
            }

            //inserto las respuestas a la pregunta en db movil
            NutriQuestExecuter.insertRespuestas(ct, idPreguntaPrevia, idPregunta, respuestas = respuestas2)

            //mando la respuesta a una pregunta a el ws
            sendUserResponses(respuesta, ct)
        }

        /**
         * consigo el id de pregunta siguiente
         */
        fun preguntaSiguiente(ct: Context, idPregunta: Int):Int{
            var idPreguntaSiguiente = 0
            val ids = numeroPreguntaSiguiente(ct, idPregunta)
            if(ids[1] == 0){
                return ids[0]
            }
            idPreguntaSiguiente = if(ids[0] != ids[1]){
                ids[1]
            } else{
                ids[0]
            }
            return idPreguntaSiguiente
        }

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