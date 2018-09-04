package com.uneatlantico.encuestas.Encuestas

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.uneatlantico.encuestas.R
import com.uneatlantico.encuestas.SettingsActivity

class InicioEncuestaActivity: AppCompatActivity(){

    lateinit var empezarEncuesta: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_encuesta)

        val idEncuesta = intent.extras.getInt("idEncuesta")

        empezarEncuesta = findViewById(R.id.botonEmpezarEncuesta)
        empezarEncuesta.setOnClickListener {
            val i = Intent(this, NutriQuestMain::class.java)
            i.putExtra("idEncuesta", idEncuesta)
            ContextCompat.startActivity(this, i, null)
            finish()
        }
    }
}