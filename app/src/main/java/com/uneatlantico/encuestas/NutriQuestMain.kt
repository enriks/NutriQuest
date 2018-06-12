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

    private lateinit var back: AppCompatImageButton
    private val fm = supportFragmentManager
    private val mHandler = Handler()
    private var doubleBackToExitPressedOnce = false
    private lateinit var nQController:NQController
    private lateinit var mensajeDespedida:TextView
    private lateinit var container:FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_quest_main)
        nQController = NQController()
        mensajeDespedida = findViewById(R.id.mensajeDespedida)
        container = findViewById(R.id.container)
        val envio = findViewById<Button>(R.id.pulsemepelotudo)
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
        }
        val idPregunta = intent.extras.getInt("idPregunta")
        //val idPregunta = data.describeContents()
        Log.d("idPregunta", idPregunta.toString())
        mensajeDespedida.alpha = 0.0F
        inicioPregunta(idPregunta)//nQController.nextQuestion(this, 0))
    }

    /*fun loadNextPregunta(){
        changeFragment(nQController.nextPregunta)
    }*/

    fun changeFragment(idPregunta: Int){
        val bundle = Bundle()

        //place holder de la clase NQcontroller
        val idActual = nQController.nextQuestion(this,idPregunta)
        if(idActual != -1){
            bundle.putInt("idPreguntaAnterior", idPregunta)
            bundle.putInt("idPreguntaActual", idActual)

            val tempfrag = QuestionFragment.newInstance()
            tempfrag.arguments = (bundle)
            openFragment(tempfrag)
        }
        else {
            //mandarTodasLasRespuestas(this)
            removeFragment()
            container.alpha = 0.0F

            //doAsync {  mensaje("termino el test", "Fin")}
            mensajeDespedida.alpha = 1.0F

        }

    }

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
                //.add(R.id.container, fragment)

                //.addToBackStack(null)
                .commit()
    }

    private fun removeFragment() {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.out_right)
                .replace(R.id.container, Fragment())
                //.add(R.id., fragment)

                //.addToBackStack(null)
                .commit()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Presionar atrás de nuevo para salir", Toast.LENGTH_SHORT).show()
        mHandler.postDelayed(mRunnable, 1000)
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
