package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.uneatlantico.encuestas.Alerta
import com.uneatlantico.encuestas.DB.Encuesta
import com.uneatlantico.encuestas.DB.NutriQuestExecuter
import com.uneatlantico.encuestas.Encuestas.InicioFragment
import com.uneatlantico.encuestas.Encuestas.NutriQuestMain
import com.uneatlantico.encuestas.R
import com.uneatlantico.encuestas.WSReceiver.encuestasNot
import org.jetbrains.anko.doAsync
import org.json.JSONArray

class EncuestasFragment : Fragment() {

    lateinit var imagenPrimeraEncuesta: ImageView
    lateinit var tarjetaPrimeraEncuesta: CardView
    lateinit var listaEncuestas: RecyclerView
    lateinit var encuestas:ArrayList<Encuesta>
    lateinit var botonMenu: ImageButton
    lateinit var bottomFragment: BottomFragment
    var conseguido = false
    private val imagenes:List<Int> = listOf(
            R.drawable.desayuno,
            R.drawable.mediamanana,
            R.drawable.comida,
            R.drawable.merienda,
            R.drawable.cena,
            R.drawable.recena,
            R.drawable.picoteo,
            R.drawable.manzanos
    )
    val preferencias = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_encuestas, container, false)

        doAsync { encuestas = loadEncuestas(); conseguido = true }
        //TODO llenar la imagen desde db
        imagenPrimeraEncuesta = view.findViewById(R.id.imagenPrimeraEncuesta)

        tarjetaPrimeraEncuesta = view.findViewById(R.id.tarjetaPrimeraEncuesta)
        tarjetaPrimeraEncuesta.setOnClickListener{ (activity as InicioActivity).alerta(1) }//startEncuesta(1)}
        listaEncuestas =  view.findViewById(R.id.listaEncuestas)

        while(!conseguido){}
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
        listaEncuestas.layoutManager = layoutManager

        val adaptador = InicioAdapter(encuestas, object : InicioAdapter.InicioAdapterListener {
            override fun loadEncuesta(position: Int) {
                (activity as InicioActivity).alerta(encuestas[position].idEncuesta)//activity.//startEncuesta(encuestas[position].idEncuesta)
            }
        })

        listaEncuestas.adapter = adaptador
        Log.d("tamanoEncuestas", encuestas.size.toString())
        preferencias?.edit()?.putInt("NEncuestasSinTerminar", encuestas.size)?.apply()
        //Log.d("heGuardado", preferencias?.getString("NEncuestasSinTerminar", "DEFAULT"))
        return view
    }

    private fun startEncuesta(idEncuesta:Int) {
        val i = Intent(this.context, NutriQuestMain::class.java)
        i.putExtra("idEncuesta", idEncuesta)
        startActivity(i)
        //finish()
    }

    fun loadEncuestas():ArrayList<Encuesta>{
        val encuestas = ArrayList<Encuesta>()
        try {
            val resp = encuestasNot(idUsuario = NutriQuestExecuter.idUsuario(this.context!!))
            val gson = Gson()
            val json = JSONArray(resp)

            for (i in 0 until json.length()) {
                val encuesta = gson.fromJson<Encuesta>(json[i].toString(), Encuesta::class.java)
                encuesta.imagen = imagenes[encuesta.idEncuesta-2]
                encuestas.add(encuesta)
            }
        }catch (e:Exception){
            Log.d("loadEncuestasExp", e.message)}
        return encuestas
    }

    companion object {
        fun newInstance(): EncuestasFragment = EncuestasFragment()
    }

}