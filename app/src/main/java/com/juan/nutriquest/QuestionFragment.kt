package com.juan.nutriquest

import android.content.ClipData
import android.content.Context
import android.net.Uri
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
import com.juan.nutriquest.NQController.Companion.formarPregunta
import com.juan.nutriquest.NutriQuestExecuter.Companion.guardarRespuesta
import com.juan.nutriquest.NutriQuestExecuter.Companion.insertRespuestas
import kotlinx.android.synthetic.main.fragment_question.*
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

    private val LIMITE_ELEGIDOS: Byte = 3
    private val MINIMOS_ELEGIDOS: Byte = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val ct = this.context
        val v = inflater.inflate(R.layout.fragment_question, container, false)
        val idPreguntaAnterior = arguments!!.getInt("idPreguntaAnterior")
        val idPregunta = arguments!!.getInt("idPreguntaActual")

        val pregunta = formarPregunta(this.context!!, idPregunta)
        //printPregunta(pregunta)
        tituloPregunta = v.findViewById(R.id.pregunta)
        tituloPregunta.text = pregunta.pregunta
        var sizeConstestadas = 0
        val respuestas = pregunta.posiblesRespuestas
        listaRespuestas = v.findViewById(R.id.listaRespuestas)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context!!)
        listaRespuestas.layoutManager = layoutManager

        val nQAdapter = NQAdapter(respuestas,object : NQAdapter.NQAdapterListener {
            override fun unCheckClick(position: Int) {
                val respuesta = respuestas[position]
                respuestas[position].contestado = 0
                sizeConstestadas --
                //Toast.makeText(ct, respuesta, Toast.LENGTH_SHORT).show()
            }

            override fun checkClick(position: Int) {
                val respuesta = respuestas[position]
                respuestas[position].contestado = 1
                sizeConstestadas ++
                //Toast.makeText(ct, respuesta, Toast.LENGTH_SHORT).show()
            }

        })
        listaRespuestas.adapter = nQAdapter

        forwardArrow = v.findViewById(R.id.nextArrow)
        forwardArrow.setOnClickListener {
            /*if(sizeConstestadas > LIMITE_ELEGIDOS || sizeConstestadas < MINIMOS_ELEGIDOS){
                Toast.makeText(ct, "no has elegido una cantidad adecuada", Toast.LENGTH_LONG).show()
            }
            else {*/
                doAsync {
                    insertRespuestas(ct!!, idPreguntaAnterior,idPregunta, NutriQuestExecuter.numeroPreguntaSiguiente(ct, idPregunta), respuestas = respuestas)
                    (activity as NutriQuestMain).changeFragment(idPregunta)
                }
            //}
        }

        return v
    }

    private fun printPregunta(pregunta: Pregunta) {
        var i = 0
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
            holder.respuestaPosible.text = listaRespuestas[position].respuesta
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

    override fun getItemCount() = listaRespuestas.size


    inner class NQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)  {
        var check: CheckBox
        val respuestaPosible: AppCompatTextView

        init {
            check = itemView.findViewById(R.id.checky) as CheckBox
            respuestaPosible = itemView.findViewById(R.id.respuestaPosible) as AppCompatTextView
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