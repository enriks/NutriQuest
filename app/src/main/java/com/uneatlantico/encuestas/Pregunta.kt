package com.uneatlantico.encuestas

import java.util.HashMap

/**
 * TODAS LAS ESTRUCTURAS DE DATOS PARA TRANSFORMAR SQL EN OBJETOS MANEJABLES
 */

/**
 *
 */
class Pregunta(map: MutableMap<String, Any?>) {
    var _id: Int by map
    var pregunta: String by map
    var fecha: String by map
    var posiblesRespuestas: ArrayList<Respuesta> by map
    var minRespuestas:Int by map
    var maxRespuestas:Int by map

    constructor(id:Int = 1, pregunta:String="", fecha:String="",posiblesRespuestas:ArrayList<Respuesta> = ArrayList(), minRespuestas:Int, maxRespuestas:Int):this(HashMap()) {
        this._id = id
        this.pregunta = pregunta
        this.fecha = fecha
        this.posiblesRespuestas = posiblesRespuestas
        this.minRespuestas = minRespuestas
        this.maxRespuestas = maxRespuestas
    }
}

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
class Respuesta(var map:MutableMap<String, Any?>){
    var _id: Int by map
    var respuesta: String by map
    var determinaCategoria: Int by map
    var categoriaVisibilidad: Int by map
    var visibilidad: Int by map
    var contestadoAnterior: Int by map
    var contestado:Int by map
    var idPreguntaSiguiente:Int by map

    constructor(_id:Int = 0, respuesta:String, categoriaVisibilidad:Int, determinaCategoria: Int,visibilidad:Int = 1, contestado:Int = 0, contestadoAnterior:Int = 0,idPreguntaSiguiente:Int = 0):this(HashMap()){
        this._id = _id
        this.respuesta = respuesta
        this.determinaCategoria = determinaCategoria
        this.categoriaVisibilidad = categoriaVisibilidad
        this.visibilidad = visibilidad
        this.contestadoAnterior = contestadoAnterior
        this.contestado = contestado
        this.idPreguntaSiguiente = idPreguntaSiguiente
    }
}

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