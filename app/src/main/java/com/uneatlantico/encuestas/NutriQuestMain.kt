package com.uneatlantico.encuestas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationManagerCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatImageButton
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.deleteAll
import org.jetbrains.anko.doAsync

class NutriQuestMain : AppCompatActivity() {

    private lateinit var back: AppCompatImageButton //icono flecha atras
    private val fm = supportFragmentManager
    private val mHandler = Handler()
    private var doubleBackToExitPressedOnce = false //boolean para controlar doble click
    private lateinit var nQController:NQController
    private lateinit var mensajeDespedida:TextView
    private lateinit var container:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_quest_main)
        nQController = NQController()
        mensajeDespedida = findViewById(R.id.mensajeDespedida)
        mensajeDespedida.alpha = 0.0F
        container = findViewById(R.id.container)
        /*val envio = findViewById<Button>(R.id.pulsemepelotudo)
        envio.alpha = 0.0F
        /*envio.setOnClickListener {
            doAsync {
                //sendPostRequest(2, applicationContext)
                //recibirPregunta(4, applicationContext)
                nQController.inicioEncuesta(applicationContext)
            }
        }*/
        back = findViewById(R.id.backEncuesta)
        //back.alpha = 0.0F
        back.setOnClickListener {
            deleteAll(this)
            changeFragment(0)
        }*/

        //Cojo el id de Pregunta que me trae la primera vez
        val idPregunta = intent.extras.getInt("idPregunta")


        inicioPregunta(idPregunta)//nQController.nextQuestion(this, 0))
    }

    /**
     *
     */
    fun changeFragment(idPregunta: Int){
        val bundle = Bundle()

        //place holder de la clase NQcontroller
        val idActual = nQController.nextQuestion(this,idPregunta)

        //abrir el fragmento con la siguiente pregunta
        if(idActual != -1){
            bundle.putInt("idPreguntaAnterior", idPregunta)
            bundle.putInt("idPreguntaActual", idActual)

            val tempfrag = QuestionFragment.newInstance()
            tempfrag.arguments = (bundle)
            openFragment(tempfrag)
        }

        //No quedan mas preguntas, se acabo la encuesta
        else {
            //mandarTodasLasRespuestas(this)
            removeFragment()
            container.alpha = 0.0F
            container.removeAllViews()
            mensajeDespedida.alpha = 1.0F

        }

    }

    /**
     * Aqui empieza la encuesta
     */
    fun inicioPregunta(idPregunta: Int){

        val bundle = Bundle()
        bundle.putInt("idPreguntaAnterior", 0)
        bundle.putInt("idPreguntaActual", idPregunta)

        val tempfrag = QuestionFragment.newInstance()
        tempfrag.arguments = (bundle)
        openFragment(tempfrag)

    }

    private fun openFragment(fragment: Fragment) {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_right, R.anim.out_left)
                .replace(R.id.container, fragment)

                .addToBackStack(null)
                .commit()
    }

    private fun removeFragment() {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.out_right)
                .replace(R.id.container, Fragment())
                .addToBackStack(null)
                .commit()
    }

    /**
     * Pulsar el boton de atras
     */
    override fun onBackPressed() {

        if(fm.backStackEntryCount <=1){
            if (doubleBackToExitPressedOnce) {
                System.exit(0)
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Presionar atrás de nuevo para salir", Toast.LENGTH_SHORT).show()
            mHandler.postDelayed(mRunnable, 1000)
        }

        else
            super.onBackPressed()


    }

    private val mRunnable = Runnable { doubleBackToExitPressedOnce = false }

    override fun onDestroy() {
        super.onDestroy()

        mHandler.removeCallbacks(mRunnable)
    }

    private fun mensaje(msg: String= "no especificado", ttl:String="titulo generico" ) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle(ttl)
        val dialog = builder.create()
        dialog.show()
    }
}
