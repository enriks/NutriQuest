package com.uneatlantico.encuestas.DB

import java.util.HashMap

/**
 * TODAS LAS ESTRUCTURAS DE DATOS PARA TRANSFORMAR SQL EN OBJETOS MANEJABLES
 */

/**
 *
 */
/*class Pregunta(map: MutableMap<String, Any?>) {
    var _id: Int by map
    var pregunta: String by map
    var fecha: String by map
    var posiblesRespuestas: ArrayList<Respuesta> by map
    var minRespuestas:Int by map
    var maxRespuestas:Int by map

    constructor(id:Int = 1, pregunta:String="", fecha:String="", posiblesRespuestas:ArrayList<Respuesta> = ArrayList(), minRespuestas:Int = 1, maxRespuestas:Int = 1):this(HashMap()) {
        this._id = id
        this.pregunta = pregunta
        this.fecha = fecha
        this.posiblesRespuestas = posiblesRespuestas
        this.minRespuestas = minRespuestas
        this.maxRespuestas = maxRespuestas
    }
}*/

data class Pregunta(var _id:Int = 1, var pregunta:String="", var fecha:String="", var posiblesRespuestas:ArrayList<Respuesta> = ArrayList(), var minRespuestas:Int = 1, var maxRespuestas:Int = 1)

/**
 * Pregunta con su visibilidad para el Fragmento
 */
class SoloPregunta(map: MutableMap<String, Any?>) {
    var _id: Int by map
    var pregunta: String by map
    var idCategoria: Int by map
    var visibilidad: Int by map
    var minRespuestas:Int by map
    var maxRespuestas:Int by map

    constructor(_id: Int = 0, pregunta: String="", idCategoria: Int=0, visibilidad: Int=1, minRespuestas:Int=0, maxRespuestas:Int=0) : this(HashMap()) {
        this._id = _id
        this.pregunta = pregunta
        this.idCategoria = idCategoria
        this.visibilidad = visibilidad
        this.minRespuestas = minRespuestas
        this.maxRespuestas = maxRespuestas
    }
}

/**
 * Respuesta para el fragmento
 */
/*class Respuesta(var map:MutableMap<String, Any?>){
    var _id: Int by map
    var respuesta: String by map
    var determinaCategoria: Int by map
    var categoriaVisibilidad: Int by map
    var visibilidad: Int by map
    var contestadoAnterior: Int by map
    var contestado:Int by map
    var idPreguntaSiguiente:Int by map

    constructor(_id:Int = 0, respuesta:String, categoriaVisibilidad:Int = -1, determinaCategoria: Int,visibilidad:Int = 1, contestado:Int = 0, contestadoAnterior:Int = 0,idPreguntaSiguiente:Int = 0):this(HashMap()){
        this._id = _id
        this.respuesta = respuesta
        this.determinaCategoria = determinaCategoria
        this.categoriaVisibilidad = categoriaVisibilidad
        this.visibilidad = visibilidad
        this.contestadoAnterior = contestadoAnterior
        this.contestado = contestado
        this.idPreguntaSiguiente = idPreguntaSiguiente
    }
}*/

data class Respuesta(var _id:Int = 0, var respuesta:String,var categoriaVisibilidad:Int? = -1,var determinaCategoria: Int?,var visibilidad:Int? = 1,var  contestado:Int = 0, var contestadoAnterior:Int? = 0,var idPreguntaSiguiente:Int = 0)

/**
 *
 */
class RespuestasUsuario(var map:MutableMap<String, Any?>){
    var _id: Int by map
    var respuesta: String by map
    var idPregunta: Int by map
    var idCategoria: Int by map
    var idPreguntaPrevia: Int by map
    var idPreguntaPosterior:Int by map
    var contestado:Int by map

    constructor( respuesta:String, idPregunta:Int, idCategoria:Int,idPreguntaPrevia: Int,visibilidad:Int = 1, contestado:Int = 0):this(HashMap()){
        //this._id = _id
        this.respuesta = respuesta
        this.idPregunta = idPregunta
        this.idCategoria =idCategoria
        this.idPreguntaPrevia = idPreguntaPrevia
        this.idPreguntaPosterior = visibilidad
        this.contestado = contestado
    }
}

data class PreguntaRaw(var _id: Int, var pregunta: String, var idPreguntaSiguiente: Int, var minRespuestas:Int, var maxRespuestas:Int)

data class RespuestaPosibleRaw(var _id:Int, var respuesta:String, var idPregunta:Int, var idCategoria:Int, var idPreguntaSiguiente: Int, var visibilidad:Int = 1, var contestado:Int = 0)

data class CategoriaRaw(var _id: Int, var categoria:String)

data class VisibilidadRaw(var _id:Int, var idElemento: Int, var tipoElemento:Int, var idCategoria: Int, var visibilidad: Int)

data class EncuestaRaw(var idEncuesta:Int, var idPrimeraPregunta:Int, var numeroPreguntas:Int, var preguntaFinal:Int, var clave:String)

data class RespuestaRaw(var idRespuesta:Int ,var idPregunta:Int, var idCategoria:Int, var idPreguntaPrevia: Int, var idPreguntaPosterior: Int, var contestado:Int = 0)