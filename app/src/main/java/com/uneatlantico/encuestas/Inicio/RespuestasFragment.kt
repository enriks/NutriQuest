package com.uneatlantico.encuestas.Inicio

import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.uneatlantico.encuestas.DB.Encuesta
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.DB.RespuestasEncuesta
import com.uneatlantico.encuestas.R
import com.uneatlantico.encuestas.WSReceiver.respuestasEncuestaT
import kotlinx.android.synthetic.main.fragment_bottom.*
import org.jetbrains.anko.doAsync
import org.json.JSONArray
import org.json.JSONObject

class RespuestasFragment : Fragment(){

    private lateinit var respuestas: ArrayList<RespuestasEncuesta>
    private lateinit var listaRespuestas:RecyclerView
    private var conseguido = false
    @SuppressLint("NewApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_respuestas, container, false)
        val idEncuesta = arguments!!.getInt("idEncuesta")

        doAsync {
            respuestas = loadRespuestas(idEncuesta)
        }
        while(!conseguido){}
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        listaRespuestas.layoutManager = layoutManager

        val adaptador = RespuestasAdapter(respuestas, ct = this.context)

        listaRespuestas.adapter = adaptador

        return v
    }

    @SuppressLint("NewApi")
    fun loadRespuestas(idEncuesta: Int):ArrayList<RespuestasEncuesta>{
        val total = ArrayList<RespuestasEncuesta>()
        val response = respuestasEncuestaT( idUsuario(this.context), idEncuesta)
        val json = JSONArray(response)
        for(i in 0 until json.length()){
            val jsonTemp = JSONObject(json[i].toString())
            val pregunta = jsonTemp.getString("pregunta")

            val respuestas = ArrayList<String>()
            val respTemp = jsonTemp.getJSONArray("respuestas")
            for(j in 0 until respTemp.length()){
                respuestas.add(respTemp[j].toString())
            }

            val respuestasComp = ArrayList<String>()
            val respComTemp = jsonTemp.getJSONArray("respuestas")
            for(j in 0 until respComTemp.length()){
                respuestasComp.add(respComTemp[j].toString())
            }
            total.add(RespuestasEncuesta(pregunta, respuestas, respuestasComp))
        }
        conseguido = true
        return total
    }

    companion object {
        fun newInstance(): RespuestasFragment = RespuestasFragment()
    }
}