package com.uneatlantico.encuestas.Inicio

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.widget.ImageView
import com.uneatlantico.encuestas.Encuestas.NutriQuestMain
import com.uneatlantico.encuestas.R

class InicioActivity : AppCompatActivity() {

    lateinit var imagenPrimeraEncuesta: ImageView
    lateinit var tarjetaPrimeraEncuesta:CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        //TODO llenar la imagen desde db
        imagenPrimeraEncuesta = findViewById(R.id.imagenPrimeraEncuesta)

        tarjetaPrimeraEncuesta = findViewById(R.id.tarjetaPrimeraEncuesta)
        tarjetaPrimeraEncuesta.setOnClickListener{startEncuesta()}
    }

    private fun startEncuesta() {
        val i = Intent(this, NutriQuestMain::class.java)
        var idEncuesta = 1
        i.putExtra("idEncuesta", idEncuesta)

        startActivity(i)
        finish()
    }

    fun loadImages(){
        //imagenPrimeraEncuesta.setImageURI()
    }
}
