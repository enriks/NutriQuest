package com.juan.nutriquest

import android.content.Context
import com.juan.nutriquest.NutriQuestExecuter.Companion.devolverRespuestasPilar
import com.juan.nutriquest.NutriQuestExecuter.Companion.numeroPreguntaActual
import com.juan.nutriquest.NutriQuestExecuter.Companion.numeroPreguntas


/**
 * 2 opciones
 */
class NQController{

    val ct:Context
    var posicionPregunta:Int = 0
    private val questionNumber:Int
    private var orden: ArrayList<Int> = ArrayList()
    /**
     *
     */
    constructor(ct:Context){
        this.ct = ct
        this.questionNumber = numeroPreguntas(ct)
        firstOrder()
    }

    /**
     * crea el array con todos los id de pregunta disponibles
     */
    fun firstOrder(){
        for (i in 1..questionNumber)
            orden.add(i)
    }

    fun secondOrder(){
        val respuestasPilar = devolverRespuestasPilar(ct)
        respuestasPilar.forEach {

        }
    }

    fun nextPregunta():Pregunta {
        val idPregunta = numeroPreguntaActual(ct)
        pasar(idPregunta)

        return createPregunta()
    }

    private fun createPregunta():Pregunta {
        return Pregunta()
    }

    /**
     * compruebo a cual pregunta deberia pasar
     */
    fun pasar(idPregunta: Int):Int{
        var numeroPregunta:Int = 0
        when(idPregunta){
            4 ->{}
            5 ->{}
            6 ->{}
            7 ->{}
            8 ->{}
            9 ->{}
            10 ->{}
            11 ->{}
            12 ->{}
            13 ->{}
            14 ->{}
            15 ->{}
            16 ->{}
            17 ->{}
            18 ->{}
            19 ->{}
            20 ->{}
            21 ->{}
            22 ->{}
        }
        return numeroPregunta
    }

}