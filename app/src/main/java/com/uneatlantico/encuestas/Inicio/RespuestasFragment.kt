package com.uneatlantico.encuestas.Inicio

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uneatlantico.encuestas.R

class RespuestasFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_respuestas, container, false)



        return v
    }

    companion object {
        fun newInstance(): RespuestasFragment = RespuestasFragment()
    }
}