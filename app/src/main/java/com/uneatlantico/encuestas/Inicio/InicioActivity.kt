package com.uneatlantico.encuestas.Inicio

import android.app.FragmentManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import com.google.gson.Gson
import com.uneatlantico.encuestas.Alerta
import com.uneatlantico.encuestas.DB.Encuesta
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.Encuestas.NutriQuestMain
import com.uneatlantico.encuestas.R
import com.uneatlantico.encuestas.WSReceiver.encuestasNot
import org.jetbrains.anko.doAsync
import org.json.JSONArray

class InicioActivity : AppCompatActivity() {

    lateinit var imagenPrimeraEncuesta: ImageView
    lateinit var tarjetaPrimeraEncuesta:CardView
    lateinit var listaEncuestas:RecyclerView
    lateinit var encuestas:ArrayList<Encuesta>
    lateinit var botonMenu: ImageButton
    lateinit var bottomFragment: BottomFragment
    val fm = supportFragmentManager
    var conseguido = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        bottomFragment = BottomFragment()
        botonMenu = findViewById(R.id.menuButton)
        botonMenu.setOnClickListener { bottomFragment.show(fm, null) }
        val tempfrag = EncuestasFragment.newInstance()
        openFragment(tempfrag)

    }

    fun alerta(idEncuesta:Int){
        Alerta(this, idEncuesta).show()
    }

    fun openFragment(fragment: Fragment) {
        fm.beginTransaction()
                //.setCustomAnimations(R.anim.enter_right, R.anim.out_left, R.anim.enter_left,R.anim.out_right)
                .replace(R.id.containerInicio, fragment)

                .addToBackStack(null)
                .commit()
    }

    /*private fun startEncuesta(idEncuesta:Int) {
        val i = Intent(this, NutriQuestMain::class.java)
        i.putExtra("idEncuesta", idEncuesta)
        startActivity(i)
        //finish()
    }

    fun loadEncuestas():ArrayList<Encuesta>{
        val encuestas = ArrayList<Encuesta>()
        try {
            val resp = encuestasNot(idUsuario = idUsuario(this))
            val gson = Gson()
            val json = JSONArray(resp)

            for (i in 0 until json.length()) {
                val encuesta = gson.fromJson<Encuesta>(json[i].toString(), Encuesta::class.java)
                encuesta.imagen = imagenes[encuesta.idEncuesta-2]
                encuestas.add(encuesta)
            }
        }catch (e:Exception){Log.d("loadEncuestasExp", e.message)}
        return encuestas
    }*/

    override fun onBackPressed() {

        if(fm.backStackEntryCount <=1){
            //if (doubleBackToExitPressedOnce) {
                System.exit(0)
                return
            //}
            /*this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Presionar atrás de nuevo para salir", Toast.LENGTH_SHORT).show()
            mHandler.postDelayed(mRunnable, 1000)*/
        }

        else {
            /*if (nQController.posicionPregunta >= 1)
                nQController.posicionPregunta--*/
            super.onBackPressed()
        }


    }
}
