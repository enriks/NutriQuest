package com.uneatlantico.encuestas.Inicio

import android.app.PendingIntent.getActivity
import android.graphics.Typeface
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import com.uneatlantico.encuestas.DB.Encuesta
import com.uneatlantico.encuestas.DB.Respuesta
import com.uneatlantico.encuestas.R

class InicioAdapter(private val encuestas: ArrayList<Encuesta>, listener: InicioAdapterListener) : RecyclerView.Adapter<InicioAdapter.InicioViewHolder>() {

    val onClickListener: InicioAdapterListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = InicioViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.encuestas_posibles, parent, false))


    override fun onBindViewHolder(holder: InicioViewHolder, position: Int) {
        //Log.d("visibilidad",listaRespuestas[position].visibilidad.toString())
        var positionTemp = 2*position
        if(positionTemp < encuestas.size){
            if(encuestas[positionTemp].terminado == 0) {

                /**
                 * tarjeta izquierda
                 */
                holder.tarjeta1.visibility = View.VISIBLE
                holder.nombreEncuesta1.text = encuestas[positionTemp].nombre
                holder.imagen1.setImageResource(encuestas[positionTemp].imagen)
                holder.tarjeta1.setOnClickListener {
                    onClickListener.loadEncuesta(positionTemp)
                }


                /**
                 * tajeta derecha
                 */
                if (positionTemp + 1 < encuestas.size) {

                    holder.tarjeta2.visibility = View.VISIBLE
                    holder.nombreEncuesta2.text = encuestas[positionTemp + 1].nombre
                    holder.imagen2.setImageResource(encuestas[positionTemp + 1].imagen)
                    holder.tarjeta2.setOnClickListener {
                        onClickListener.loadEncuesta(positionTemp + 1)
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount():Int{

        var sizeT = encuestas.size
        var size = if(sizeT%2 == 0) (sizeT/2) else (sizeT/2)+1
        return size
    }

    inner class InicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {

        val tarjeta1:CardView
        val tarjeta2:CardView
        val nombreEncuesta1: AppCompatTextView
        val nombreEncuesta2: AppCompatTextView
        val imagen1:ImageView
        val imagen2:ImageView

        init {

            tarjeta1 = itemView.findViewById(R.id.tarjeta1)
            tarjeta2 = itemView.findViewById(R.id.tarjeta2)
            nombreEncuesta1 = itemView.findViewById(R.id.titulo1) as AppCompatTextView
            nombreEncuesta2 = itemView.findViewById(R.id.titulo2) as AppCompatTextView
            imagen1 = itemView.findViewById(R.id.imagen1)
            imagen2 = itemView.findViewById(R.id.imagen2)

        }

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

    }

    interface InicioAdapterListener {
        fun loadEncuesta(position: Int)
//        fun unCheckClick(position: Int)
        //fun viewMore(v:View, position: Int)
    }



}