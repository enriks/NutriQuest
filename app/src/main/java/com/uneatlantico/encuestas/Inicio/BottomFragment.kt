package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.CoordinatorLayout
import android.widget.LinearLayout
import com.uneatlantico.encuestas.DB.NutriQuestExecuter
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.R
import kotlin.reflect.KClass


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MenuFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BottomFragment : BottomSheetDialogFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.Encuestas ->{(activity as InicioActivity).openFragmentSure(EncuestasFragment.newInstance())}
            R.id.respuestasAnteriores ->{(activity as InicioActivity).openFragmentSure(EncuestasFragment.newInstance())}
            R.id.Ajustes -> {(activity as InicioActivity).startAjustes()}
        }
    }

    private lateinit var numeroEncuestas:TextView
    private lateinit var menuGoogleUserName:TextView
    private lateinit var menuGoogleUserEmail:TextView
    private lateinit var menuGoogleUserImage:ImageView
    private lateinit var encuestasPress:LinearLayout
    private lateinit var respuestasAnterioresPress:LinearLayout
    private lateinit var horarioPress:LinearLayout
    private lateinit var settingsPress:LinearLayout
    private lateinit var campusPress:LinearLayout
    private lateinit var nutriQuestExecuter: NutriQuestExecuter

    val preferencias = activity?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_bottom, container, false)
        numeroEncuestas = v.findViewById(R.id.NEncuestasTerminar)
        numeroEncuestas.text = preferencias?.getInt("NEncuestasSinTerminar", 0).toString() + "Encuestas por terminar"
        nutriQuestExecuter = NutriQuestExecuter(this.context!!)
        menuGoogleUserName = v.findViewById(R.id.menuGoogleUserName)
        menuGoogleUserEmail = v.findViewById(R.id.menuGoogleUserEmail)
        menuGoogleUserImage = v.findViewById(R.id.menuGoogleUserImage)

        encuestasPress = v.findViewById(R.id.Encuestas)
        encuestasPress.setOnClickListener(this)

        respuestasAnterioresPress = v.findViewById(R.id.respuestasAnteriores)
        respuestasAnterioresPress.setOnClickListener(this)

        horarioPress = v.findViewById(R.id.nose)
        horarioPress.setOnClickListener(this)

        //TODO cambiar lanzar actividad a campus cuando este completa
        campusPress = v.findViewById(R.id.inf)
        campusPress.setOnClickListener(this)

        settingsPress = v.findViewById(R.id.Ajustes)
        settingsPress.setOnClickListener(this)
        initMenu()
        return v
    }

    private fun Launch(ina: KClass<*>, ham_option_title:String) {
        val i = Intent(this.context, ina.java)
        i.putExtra("title", ham_option_title)
        //finish()  //Kill the activity from which you will go to next activity
        startActivityForResult(i, 0)
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog

        if (dialog != null) {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = convertDpToPixel(400F)
        }
        val view = view
        view!!.post {
            val parent = view.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as BottomSheetBehavior<*>?
            bottomSheetBehavior!!.peekHeight = view.measuredHeight

            parent.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun initMenu() {
        nutriQuestExecuter.openRDB()
        val usuario = nutriQuestExecuter.getUsuario()
        menuGoogleUserName.text = usuario.nombre //nombre
        menuGoogleUserEmail.text = usuario.email //mail
        if(usuario.photoUrl != "")
            Picasso.get().load(usuario.photoUrl).into(menuGoogleUserImage)
        nutriQuestExecuter.closeDB()
    }

    fun convertDpToPixel(dp: Float): Int {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 200f)
        return Math.round(px)
    }

    companion object {
        fun newInstance(): BottomFragment = BottomFragment()
    }
}