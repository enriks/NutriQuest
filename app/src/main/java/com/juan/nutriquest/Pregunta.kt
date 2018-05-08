package com.juan.nutriquest

import java.util.HashMap

class Pregunta(var map: MutableMap<String, Any?>) {
    var _id: Int by map
    var pregunta: String by map
    var fecha: String by map
    var posiblesRespuestas: List<String> by map

    constructor(id:Int = 1, pregunta:String="", fecha:String="", posiblesRespuestas:List<String> = emptyList()):this(HashMap()) {
        this._id = id
        this.pregunta = pregunta
        this.fecha = fecha
        this.posiblesRespuestas = posiblesRespuestas
    }
}