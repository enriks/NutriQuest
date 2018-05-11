package com.juan.nutriquest

import android.content.Context
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
            preguntaSiguiente = numeroPreguntaSiguiente(ct, preguntaSiguiente)
        } while(!avanzar(preguntaSiguiente))
        return preguntaSiguiente
    }

    private fun avanzar(numeroPreguntaSiguiente: Int):Boolean {
        var avanzar:Boolean = true

        return avanzar
    }

}