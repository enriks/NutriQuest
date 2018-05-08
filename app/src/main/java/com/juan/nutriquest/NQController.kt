package com.juan.nutriquest

class NQController{

    /**
     * orden
     *
     */
    constructor()
    fun guardarPregunta(){

    }

    fun devolucionPregunta(idPregunta:Int, respuesta:String):Int{
        var nextPregunta:Int = idPregunta
        do{
            nextPregunta ++
        } while(!pasar(nextPregunta))

        return nextPregunta
    }

    fun pasar(idPregunta: Int):Boolean{
        when(idPregunta){
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
        return true
    }

}