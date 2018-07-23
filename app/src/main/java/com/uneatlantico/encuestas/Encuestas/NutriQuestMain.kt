package com.uneatlantico.encuestas.Encuestas

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatImageButton
import android.support.v7.widget.CardView
import android.util.Log
import android.view.View
import android.widget.*
import com.uneatlantico.encuestas.Inicio.InicioActivity
import com.uneatlantico.encuestas.R
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import kotlin.concurrent.thread

class NutriQuestMain : AppCompatActivity() {

    private lateinit var back: AppCompatImageButton //icono flecha atras
    private val fm = supportFragmentManager
    private val mHandler = Handler()
    private var doubleBackToExitPressedOnce = false //boolean para controlar doble click
    lateinit var nQController: NQController

    private lateinit var container:FrameLayout
    private lateinit var progress_bar: CardView
    private lateinit var bar: CardView
    private var questionNumber = 0
    private var numeroPregunta: Int = 0
    private var idEncuesta:Int = 1
    //private var fragmentTag:Int = 0
    private lateinit var reiniciar: CardView
    private var atras = false
    private val reintentarConexion = Thread {

        //try {

        while (!inicioEncuesta(idEncuesta)) {
            Thread.sleep(3000)

            Log.d("reintentando", "si")
        }
        //}catch (e:Exception){Log.d("threadErrorReint", e.message)}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_quest_main)
        idEncuesta = intent.extras.getInt("idEncuesta")
        nQController = NQController(this.applicationContext, idEncuesta)

        //inicioFrag = InicioFragment()
        container = findViewById(R.id.container)
        progress_bar = findViewById(R.id.progress_bar)
        bar = findViewById(R.id.perma_bar)

        //TODO esconder boton de reiniciar si no tiene sentido
        //TODO calcular cual es la primera pregunta de la encuesta y volver ahi en lugar de 1
        reiniciar = findViewById(R.id.backEncuesta)
        reiniciar.alpha = 0.0F
        reiniciar.visibility = View.GONE
        reiniciar.setOnClickListener {
            atras = true
            Toast.makeText(this, "Ya puede ir atrás", Toast.LENGTH_SHORT).show()

        }

        carga()
        //while (!(::nQController.isInitialized)) {}
        doAsync { if(!inicioEncuesta(idEncuesta)) if(!reintentarConexion.isAlive) reintentarConexion.start() }
        /*doAsync {
            val comenzo = inicioEncuesta(idEncuesta)
            if(!comenzo) {
                if (!reintentarConexion.isAlive)
                    reintentarConexion.start()
            }
        }*/
    }

    fun changeFragment(idPregunta: Int){
        val bundle = Bundle()


        doAsync {
            if(nQController.recibirPreguntaX(idPregunta) == -1){

                val tempfrag = EndFragment.newInstance()
                openFragment(tempfrag)
                reiniciar.alpha = 0.0F
                percentajeLeft(-1)
                Thread.sleep(2000)
                val intent = Intent(applicationContext, InicioActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                //abrir el fragmento con la siguiente pregunta
                bundle.putInt("idPreguntaAnterior", idPregunta)

                val tempfrag = QuestionFragment.newInstance(nQController)
                //tempfrag.setController()
                tempfrag.arguments = (bundle)
                openFragment(tempfrag)
                reiniciar.alpha = 1.0F
                percentajeLeft(idPregunta)
            }
        }

    }

    /**
     * Aqui empieza la encuesta
     */
    fun inicioEncuesta(idEncuesta: Int):Boolean{

        val bundle = Bundle()
        bundle.putInt("idPreguntaAnterior", 0)
        bundle.putInt("idEncuesta", idEncuesta)
        var inicio = true


        var resultadoInicio = 2
        try{
            if(::nQController.isInitialized)
                resultadoInicio = nQController.primeraConexion(idEncuesta)

        }catch (e:Exception){Log.d("excepcionicioENc", e.toString())}
        if( resultadoInicio == -1){
            val tempfrag = EndFragment.newInstance()
            openFragment(tempfrag)
            reiniciar.alpha = 0.0F
            percentajeLeft(questionNumber)
        }
        else if(resultadoInicio == 0){
            //carga("Espere por favor")
            questionNumber = nQController.numeroPreguntas
            percentajeLeft(5)
            val tempfrag = QuestionFragment.newInstance(nQController)
            //tempfrag.setController(nQController)
            Thread.sleep(1000)
            tempfrag.arguments = (bundle)
            resetFragment(tempfrag)
            reiniciar.alpha = 1.0F
        }
        else {

            inicio = false
        }
        Log.d("resultadoInicio", resultadoInicio.toString())
        return inicio
    }


    private fun openFragment(fragment: Fragment) {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.out_left, R.anim.enter_left,R.anim.out_right)
                .replace(R.id.container, fragment)

                .addToBackStack(null)
                .commit()
    }

    private fun resetFragment(fragment: Fragment) {
        fm.beginTransaction()
                .setCustomAnimations(0, 0, R.anim.enter_left,R.anim.out_right)
                .replace(R.id.container, fragment)

                .addToBackStack(null)
                .commit()

    }

    private fun removeFragment() {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.out_right)
                .replace(R.id.container, Fragment())
                //.addToBackStack(null)
                .commit()
    }


    override fun onBackPressed() {
        finish()
    }

    private val mRunnable = Runnable { doubleBackToExitPressedOnce = false }

    override fun onDestroy() {
        super.onDestroy()

        mHandler.removeCallbacks(mRunnable)
    }

    private fun carga(texto:String = "Cargando la encuesta..."){

        val bundle = Bundle()
        bundle.putString("texto", texto)

        val tempfrag = InicioFragment.newInstance()
        tempfrag.arguments = (bundle)
        openFragment(tempfrag)
    }

    private fun percentajeLeft(idPregunta: Int){
        val anchMax = bar.width
        val questionNumber = nQController.numeroPreguntas
        val actual = nQController.numeroPregunta

        if(questionNumber == 0 || actual == questionNumber || actual == 0 || actual ==-1 || idPregunta == -1)
            progress_bar.layoutParams.width = bar.width
        else {
            val avance = anchMax / questionNumber
            /*Log.d("avanceT", "anchMax: $anchMax / questionN: $questionNumber")
            Log.d("avanceR", avance.toString())
            Log.d("avance",(nQController.posicionPregunta+actual).toString())*/
            progress_bar.layoutParams.width = actual*avance//(nQController.posicionPregunta+actual) * avance
        }
    }

    private fun mensaje(msg: String= "no especificado", ttl:String="titulo generico" ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle(ttl)
        val dialog = builder.create()
        dialog.show()
    }
}
