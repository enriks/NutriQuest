package com.uneatlantico.encuestas.notificaciones

class NotificationBuilder {

    val _id:Int
    val pregunta:String

    constructor(idPregunta: Int){
        this._id = idPregunta
        this.pregunta = setPregunta()
    }

    private fun setPregunta():String{
        return ""
    }
}