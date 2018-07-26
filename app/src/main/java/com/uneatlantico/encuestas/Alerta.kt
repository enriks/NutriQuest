package com.uneatlantico.encuestas

import android.view.Window.FEATURE_NO_TITLE
import android.os.Bundle
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.CardView
import android.view.View
import android.view.Window
import android.widget.Button
import com.uneatlantico.encuestas.Encuestas.NutriQuestMain
import com.uneatlantico.encuestas.Inicio.RespuestasFragment


class Alerta(var c: Activity, val idEncuesta:Int) : Dialog(c), android.view.View.OnClickListener {

    var d: Dialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_alert)
        val verRespuestas: CardView = findViewById(R.id.verRespuestas)
        verRespuestas.setOnClickListener(this)
        val continuarEncuesta: CardView = findViewById(R.id.continuarEncuesta)
        continuarEncuesta.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.verRespuestas -> {

                val tempfrag = RespuestasFragment.newInstance()
                val bundle = Bundle()
                bundle.putInt("idEncuesta", idEncuesta)
                tempfrag.arguments = (bundle)
                c.fragmentManager.beginTransaction().replace(R.id.containerInicio, tempfrag).commit()
            }

            R.id.continuarEncuesta -> {
                val i = Intent(this.context, NutriQuestMain::class.java)
                i.putExtra("idEncuesta", idEncuesta)
                startActivity(this.context, i, null)
            }
        }
        dismiss()
    }
}