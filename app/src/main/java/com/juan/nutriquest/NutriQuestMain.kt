package com.juan.nutriquest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.widget.Toast

class NutriQuestMain : AppCompatActivity() {

    private val fm = supportFragmentManager
    private val mHandler = Handler()
    private var doubleBackToExitPressedOnce = false
    private lateinit var nQController:NQController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutri_quest_main)
        nQController = NQController()

        changeFragment(0)//nQController.nextQuestion(this, 0))
    }

    /*fun loadNextPregunta(){
        changeFragment(nQController.nextPregunta)
    }*/

    fun changeFragment(idPregunta: Int){
        val bundle = Bundle()

        //place holder de la clase NQcontroller
        val idActual = nQController.nextQuestion(this,idPregunta)
        bundle.putInt("idPreguntaAnterior", idPregunta)
        bundle.putInt("idPreguntaActual", idActual)

        val tempfrag = QuestionFragment.newInstance()
        tempfrag.arguments = (bundle)
        openFragment(tempfrag)
    }

    private fun openFragment(fragment: Fragment) {
        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_left, R.anim.out_right)
                .replace(R.id.container, fragment)
                //.add(R.id.container, fragment)

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
}
