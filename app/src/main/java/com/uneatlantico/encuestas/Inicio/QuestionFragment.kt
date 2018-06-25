package com.uneatlantico.encuestas.Inicio

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.uneatlantico.encuestas.DB.Pregunta
import com.uneatlantico.encuestas.DB.Respuesta
import com.uneatlantico.encuestas.R
import org.jetbrains.anko.doAsync


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [QuestionFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [QuestionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class QuestionFragment : Fragment() {

    private lateinit var listaRespuestas:RecyclerView
    private lateinit var tituloPregunta:TextView
    private lateinit var forwardArrow:ImageView
    private val posicionClick = ArrayList<Int>()
    private var LIMITE_ELEGIDOS: Int = 0
    private var MINIMOS_ELEGIDOS: Int = 0
    private lateinit var cnt: NQController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_question, container, false)

        var pregunta = Pregunta()
        val ct = this.context
        cnt = NQController(ct!!)

        val idPreguntaAnterior = arguments!!.getInt("idPreguntaAnterior")
        val idPregunta = arguments!!.getInt("idPreguntaActual")

        //if(idPreguntaAnterior != 0)
            pregunta = cnt.formarPregunta(idPregunta)
        //else pregunta = cnt.primeraConexion(idPregunta)
        val respuestas = pregunta.posiblesRespuestas

        LIMITE_ELEGIDOS = pregunta.maxRespuestas
        MINIMOS_ELEGIDOS = pregunta.minRespuestas

        Log.d("min-max", "$MINIMOS_ELEGIDOS  - $LIMITE_ELEGIDOS")

        tituloPregunta = v.findViewById(R.id.pregunta)
        tituloPregunta.text = pregunta.pregunta

        listaRespuestas = v.findViewById(R.id.listaRespuestas)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context!!)
        listaRespuestas.layoutManager = layoutManager

        val nQAdapter = NQAdapter(respuestas, object : NQAdapter.NQAdapterListener {
            override fun unCheckClick(position: Int) {
                respuestas[position].contestado = 0
                posicionClick.remove(position)
                verFlecha()

            }

            override fun checkClick(position: Int) {

                posicionClick.add(position)
                Log.d("elementos", posicionClick.toString())
                if (posicionClick.size > LIMITE_ELEGIDOS) {
                    var algo = posicionClick[0]
                    respuestas[algo].contestado = 0
                    listaRespuestas.adapter.notifyItemChanged(algo)//notifyDataSetChanged()
                    posicionClick.removeAt(0)
                }

                respuestas[position].contestado = 1

                verFlecha()
            }

        })
        listaRespuestas.adapter = nQAdapter

        forwardArrow = v.findViewById(R.id.nextArrow)
        //forwardArrow.setImageResource(R.drawable.baseline_arrow_forward_black_24dp)
        forwardArrow.setOnClickListener {

            //compruebo si puede avanzar con la cantidad de preguntas contestadas
            if( posicionClick.size < MINIMOS_ELEGIDOS){ //||sizeConstestadas > LIMITE_ELEGIDOS){
                Toast.makeText(this.context, "no has elegido una cantidad adecuada", Toast.LENGTH_LONG).show()
            }

            //termino la pregunta
            else {

                try {
                    forwardArrow.alpha = 0.0F
                    forwardArrow.isClickable = false
                } catch (excp:Exception){}

                doAsync {

                    //envio las respuestas
                    cnt.manejarRespuestas(idPreguntaAnterior, idPregunta, respuestas)
                }
                (activity as NutriQuestMain).changeFragment(idPregunta)
            }
        }

        if(posicionClick.size<MINIMOS_ELEGIDOS){
            forwardArrow.alpha = 0.0F
            forwardArrow.isClickable = false
        }

        return v
    }

    private fun verFlecha(){

       if( posicionClick.size in MINIMOS_ELEGIDOS..LIMITE_ELEGIDOS){
            forwardArrow.alpha = 1.0F
            forwardArrow.isClickable = true
        }
        else{
            forwardArrow.alpha = 0.0F
            forwardArrow.isClickable = false
        }
    }

    private fun printPregunta(pregunta: Pregunta) {
        var i = 0
        Log.d("pregunta, ", pregunta.posiblesRespuestas.toString())
        pregunta.posiblesRespuestas.forEach {
            Log.d("respuesta$i", it.respuesta + " en " + it.visibilidad)
        }
    }

    companion object {
        fun newInstance(): QuestionFragment = QuestionFragment()
    }
}

class NQAdapter : RecyclerView.Adapter<NQAdapter.NQViewHolder> {

    private val listaRespuestas: ArrayList<Respuesta>
    val onClickListener: NQAdapterListener

    constructor(listaRespuestas: ArrayList<Respuesta>, listener: NQAdapterListener) {
        this.listaRespuestas = listaRespuestas
        this.onClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = NQViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.respuestas_posibles, parent, false))


    override fun onBindViewHolder(holder: NQViewHolder, position: Int) {

        if(listaRespuestas[position].visibilidad == 1) {

            holder.bloque.visibility = View.VISIBLE
            //Log.d("hola", listaRespuestas[position].respuesta)
            holder.respuestaPosible.text = listaRespuestas[position].respuesta

            /**
             * feedback al usuario sobre respuesta ya respondida anteriormente
             */
            if(listaRespuestas[position].contestadoAnterior == 1) {
                holder.respuestaPosible.setTextColor(Color.BLUE)
            }

            holder.check.isChecked = (listaRespuestas[position].contestado == 1)

            /**
             * click del check
             */
            holder.setOnClickListener(View.OnClickListener {
                holder.check.isChecked = !(holder.check.isChecked)
                if (holder.check.isChecked) {
                    onClickListener.checkClick(position)
                } else {
                    onClickListener.unCheckClick(position)
                }
            })
        }
    }

    override fun getItemCount():Int{
        /*var size = 0
        listaRespuestas.forEach{
            if(it.visibilidad == 1)
                size ++
        }
        if(size == 1)
            size = 2*/
        return listaRespuestas.size
    }

    inner class NQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        val check: CheckBox
        val respuestaPosible: AppCompatTextView
        val separador:ImageView
        val bloque:LinearLayout

        init {
            bloque = itemView.findViewById(R.id.bloque)
            check = itemView.findViewById(R.id.checky) as CheckBox
            respuestaPosible = itemView.findViewById(R.id.respuestaPosible) as AppCompatTextView
            separador = itemView.findViewById(R.id.separador)
            check.isClickable = false
        }

        fun setOnClickListener(onClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onClickListener)
        }

    }

    interface NQAdapterListener {
        fun checkClick(position: Int)
        fun unCheckClick(position: Int)
        //fun viewMore(v:View, position: Int)
    }

}