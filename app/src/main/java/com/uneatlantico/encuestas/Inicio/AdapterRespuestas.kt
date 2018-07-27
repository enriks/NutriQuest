package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.uneatlantico.encuestas.DB.RespuestasEncuesta
import com.uneatlantico.encuestas.R

class RespuestasAdapter(private val respuestas: ArrayList<RespuestasEncuesta>, private val ct:Context) : RecyclerView.Adapter<RespuestasAdapter.RespuestasViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RespuestasViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pregunta_respuesta_encuesta_posible, parent, false))

    override fun onBindViewHolder(holder: RespuestasViewHolder, position: Int) {
        holder.Pregunta.text = respuestas[position].Pregunta
        var texto = ""

        for(i in 0 until respuestas[position].respuestas.size){
            texto += respuestas[position].respuestas[i]
            if(respuestas[position].respuestasComp[i] != "")
                texto += " ==> ${respuestas[position].respuestasComp[i]}"
            if(i+1 < respuestas[position].respuestas.size)
                texto += "\n"
        }
        holder.respuestas.text = texto
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount():Int{

        var size = respuestas.size


        //Log.d("tamañoRespuestas", size.toString())
        return size
    }

    inner class RespuestasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val Pregunta:TextView
        val respuestas:TextView
        init {
            Pregunta = itemView.findViewById(R.id.pregunta)
            respuestas = itemView.findViewById(R.id.listaRespuestas)
        }
    }
}

class RAdapter(private val respuestas: RespuestasEncuesta) : RecyclerView.Adapter<RAdapter.RViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.batata_drita, parent, false))
    override fun onBindViewHolder(holder: RViewHolder, position: Int) {
        holder.respuesta.text = respuestas.respuestas[position]
        if(respuestas.respuestasComp[position] != ""){
            holder.respuestaComp.visibility = View.VISIBLE
            //holder.respuestaComp.text = respuestas.respuestasComp[position]
        }
    }

    override fun getItemId(position: Int): Long = position.toLong()
    override fun getItemViewType(position: Int): Int = position
    override fun getItemCount():Int {
        val size = respuestas.respuestas.size
        Log.d("tamano",size.toString())
        return size}

    inner class RViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val respuesta:TextView = itemView.findViewById(R.id.respuesta)
        val respuestaComp:TextView = itemView.findViewById(R.id.respuestaComp)
    }
}