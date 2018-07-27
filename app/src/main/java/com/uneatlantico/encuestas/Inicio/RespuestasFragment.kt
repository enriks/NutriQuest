package com.uneatlantico.encuestas.Inicio

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.DB.RespuestasEncuesta
import com.uneatlantico.encuestas.R
import com.uneatlantico.encuestas.WSReceiver.respuestasEncuestaT
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject

class RespuestasFragment : Fragment(){

    private lateinit var respuestasX: ArrayList<RespuestasEncuesta>
    private lateinit var listaRespuestas:RecyclerView
    private var conseguido = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_respuestas, container, false)
        val idEncuesta = arguments!!.getInt("idEncuesta")

        /*doAsync {

        }
        while(!conseguido){}*/
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        listaRespuestas = v.findViewById(R.id.listaPreguntasYRespuestas)
        listaRespuestas.layoutManager = layoutManager

        val adaptador = RespuestasAdapter(respuestasX, this.context!!)

        listaRespuestas.adapter = adaptador

        return v
    }

    fun interPretarRespuestas(idEncuesta: Int, idUsuario:String){
        val response = respuestasEncuestaT( idUsuario, idEncuesta)
        try {
            val json = JSONArray(response)
            loadRespuestas(json)
        }catch (e:Exception){val json = JSONObject(response)
            loadRespuesta(json)
        }

    }

    fun loadRespuestas(json:JSONArray){
        val total = ArrayList<RespuestasEncuesta>()

        for(i in 0 until json.length()){
            val jsonTemp = JSONObject(json[i].toString())
            val pregunta = jsonTemp.getString("pregunta")
            Log.d("pregunta",pregunta)
            val respuestas = ArrayList<String>()
            val respTemp = jsonTemp.getJSONArray("respuestas")
            for(j in 0 until respTemp.length()){
                respuestas.add(respTemp[j].toString())

            }
            Log.d("respuestas",respuestas.toString())
            val respuestasComp = ArrayList<String>()
            val respComTemp = jsonTemp.getJSONArray("respuestasComp")
            for(j in 0 until respComTemp.length()){
                respuestasComp.add(respComTemp[j].toString())
            }
            total.add(RespuestasEncuesta(pregunta, respuestas, respuestasComp))
        }
        conseguido = true
        respuestasX = total
        //return total
    }

    fun loadRespuesta(json:JSONObject){
        val total = ArrayList<RespuestasEncuesta>()

        val jsonTemp = JSONObject(json.toString())
        val pregunta = jsonTemp.getString("pregunta")
        Log.d("pregunta",pregunta)
        val respuestas = ArrayList<String>()
        val respTemp = jsonTemp.getJSONArray("respuestas")
        for(j in 0 until respTemp.length()){
            respuestas.add(respTemp[j].toString())

        }
        Log.d("respuestas",respuestas.toString())
        val respuestasComp = ArrayList<String>()
        val respComTemp = jsonTemp.getJSONArray("respuestasComp")
        for(j in 0 until respComTemp.length()){
            respuestasComp.add(respComTemp[j].toString())
        }
        total.add(RespuestasEncuesta(pregunta, respuestas, respuestasComp))

        conseguido = true
        respuestasX = total

    }

    companion object {
        fun newInstance(): RespuestasFragment = RespuestasFragment()
    }
}