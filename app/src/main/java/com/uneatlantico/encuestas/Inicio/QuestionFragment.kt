package com.uneatlantico.encuestas.Inicio

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
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
import android.widget.Toast





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
    private lateinit var pregunta:Pregunta


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_question, container, false)

        val idEncuesta = arguments!!.getInt("idEncuesta")
        //Log.d("idEncuestaFrag", idEncuesta.toString())
        val ct = this.context
        //cnt = NQController(ct!!, idEncuesta)


        val idPreguntaAnterior = arguments!!.getInt("idPreguntaAnterior")
        //val idPregunta = arguments!!.getInt("idPreguntaActual")

        //if(idPreguntaAnterior != 0)
        pregunta = cnt.preguntias[cnt.idPreguntas[cnt.posicionPregunta]]!!//pregunta = cnt.preguntas[cnt.posicionPregunta] as Pregunta//cnt.formarPregunta(idPregunta)
        //else
            //doAsync { if(idPreguntaAnterior == 0) cnt.primeraConexion(idEncuesta) else cnt.recibirPreguntaX(idPregunta)}
        val idPregunta = pregunta._id
        //printPregunta(pregunta)
        val respuestas = pregunta.posiblesRespuestas
        //doAsync { cnt.formarPregunta2(idPregunta)}
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

                doAsync {

                    //envio las respuestas
                    //val continuar = if(posicionClick.size>0) cnt.manejarRespuestas(idPreguntaAnterior, idPregunta, respuestas) else 1
                    val continuar = cnt.manejarRespuestas(idPreguntaAnterior, idPregunta, respuestas)
                    if(continuar == 1) {
                        (activity as NutriQuestMain).changeFragment(idPregunta)

                        try {
                            forwardArrow.alpha = 0.0F
                            forwardArrow.isClickable = false
                        } catch (excp:Exception){}
                    }
                    else {
                        Log.d("noMandaRespuesta", "idk")
                        postToastMessage("Reintentelo de nuevo por favor")
                    }
                }
            }
        }

        if(posicionClick.size<MINIMOS_ELEGIDOS){
            forwardArrow.alpha = 0.0F
            forwardArrow.isClickable = false
        }

        return v
    }

    fun setController(nqController: NQController){cnt = nqController}

    private fun verFlecha(){
        //Log.d("flechaAvvanze", "minimo $MINIMOS_ELEGIDOS - maximo $LIMITE_ELEGIDOS")
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

    fun postToastMessage(message: String) {
        val handler = Handler(Looper.getMainLooper())

        handler.post(Runnable { Toast.makeText(context, message, Toast.LENGTH_SHORT).show() })
    }

    private fun mensaje(msg: String= "no especificado", ttl:String="titulo generico" ) {
        val builder = AlertDialog.Builder(this.context!!)
        builder.setMessage(msg).setTitle(ttl)
        val dialog = builder.create()
        dialog.show()
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
        //Log.d("visibilidad",listaRespuestas[position].visibilidad.toString())
        if(listaRespuestas[position].visibilidad == 1) {

            holder.bloque.visibility = View.VISIBLE
            //Log.d("hola", listaRespuestas[position].respuesta)
            holder.respuestaPosible.text = listaRespuestas[position].respuesta

            /**
             * feedback al usuario sobre respuesta ya respondida anteriormente
             */
            if(listaRespuestas[position].contestadoAnterior == 1) {
                holder.respuestaPosible.setTypeface(holder.respuestaPosible.typeface, Typeface.BOLD)//setTextColor(Color.BLUE)
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