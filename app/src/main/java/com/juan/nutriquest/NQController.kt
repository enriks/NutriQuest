package com.juan.nutriquest

import android.content.Context
import android.util.Log
import com.juan.nutriquest.NutriQuestExecuter.Companion.getCategoriasUsuario
import com.juan.nutriquest.NutriQuestExecuter.Companion.getPregunta
import com.juan.nutriquest.NutriQuestExecuter.Companion.getRespuestas
import com.juan.nutriquest.NutriQuestExecuter.Companion.insertRespuestas
import com.juan.nutriquest.NutriQuestExecuter.Companion.numeroPreguntaSiguiente

/**
 * 2 opciones
 */
class NQController{

    /**
     * determina cual sera la siguiente pregunta
     */
    fun nextQuestion(ct:Context, idPregunta: Int):Int {
        var preguntaSiguiente = idPregunta
        do {
            Log.d("preguntaSiguiente", preguntaSiguiente.toString())
            preguntaSiguiente = numeroPreguntaSiguiente(ct, preguntaSiguiente)
        } while(seguir(ct, preguntaSiguiente))
        //formarPregunta(ct, preguntaSiguiente)
        return preguntaSiguiente
    }

    /**
     * Une las preguntas con las respuestas validas
     */
    fun seguir(ct: Context, idPregunta: Int):Boolean{
        var ver = false
        val categorias = getCategoriasUsuario(ct)
        val pregunta = getPregunta(ct, idPregunta)
        //val respuestas = getRespuestas(ct, idPregunta)
        for(i in 0 until categorias.size){
            //Log.d("comparacion")
            if(pregunta.idCategoria == categorias[i] && pregunta.idCategoria != 0) {
                Log.d("visibilidad", pregunta.visibilidad.toString())
                if (pregunta.visibilidad == 2) {
                    //insertRespuestas(ct, 0,idPregunta,numeroPreguntaSiguiente(ct, idPregunta),respuestas)
                    ver = true
                }
            }
            else
                if(pregunta.visibilidad == 1)
                    ver = true
        }
        return ver
    }
    companion object {

        /**
         * visibilidad 1 es que es visible
         * visibilidad 2 es que no lo es
         * visibilidad 0 es que no tiene un valor definido
         */
        fun formarPregunta(ct:Context, idPregunta: Int):Pregunta{
            var preguntaCompleta:Pregunta
            val categorias = getCategoriasUsuario(ct)
            val pregunta = getPregunta(ct, idPregunta)
            val respuestas = getRespuestas(ct, idPregunta)
            Log.d("categorias", categorias.toString())
            //seteo la visibilidad de cada respuesta
            respuestas.forEach {
                //Log.d("idk", it.respuesta + " y " + it.categoriaVisibilidad)
                if(categorias.isEmpty())
                    it.visibilidad = 1
                else {
                    for (i in 0 until categorias.size) {
                        if (it.categoriaVisibilidad != categorias[i] && it.categoriaVisibilidad != 0) {
                            when (it.visibilidad){
                                2 -> it.visibilidad = 1
                                1 -> it.visibilidad = 2
                                else -> it.visibilidad = 1
                            }
                        }
                        else
                            if(it.visibilidad == 0)
                                it.visibilidad = 1
                    }
                }
            }
            preguntaCompleta = Pregunta(pregunta._id, pregunta = pregunta.pregunta, posiblesRespuestas = respuestas)
            return preguntaCompleta
        }

    }
}