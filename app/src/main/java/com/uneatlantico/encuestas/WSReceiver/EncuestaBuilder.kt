package com.uneatlantico.encuestas.WSReceiver

import android.content.Context
import android.util.Log
import com.uneatlantico.encuestas.DB.*

class EncuestaBuilder {

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
            while (false) {
                guardarPreguntaCompleta()
            }
            exq.closeDB()
        } catch (e:Exception){
            Log.d("guardarEncuestaExcp", e.message)}
    }

    fun guardarPreguntaCompleta(){
        val preguntaTemp = recibirPregunta( idPregunta, ct )
        guardarPregunta()
        guardarRespuestas()
        guardarCategorias()
        guardarVisibilidad()
        //idPregunta =
    }

    /**
     * guardar en DB pregunta Completa
     */
    fun guardarPregunta(){

        val pregunta: PreguntaRaw
        /*pregunta = PreguntaRaw(pregunta)
        exq.setPregunta(pregunta)*/
    }

    fun guardarRespuestas(){
        val respuestas:ArrayList<RespuestaPosibleRaw> = ArrayList()
        if(respuestas.isEmpty())
            exq.setRespuesta(respuestas)
    }

    fun guardarCategorias(){
        val categorias:ArrayList<CategoriaRaw> = ArrayList()
        if(categorias.isEmpty())
            exq.setCategorias(categorias)
    }

    fun guardarVisibilidad(){

    }

    fun generarPregunta(){

    }
}