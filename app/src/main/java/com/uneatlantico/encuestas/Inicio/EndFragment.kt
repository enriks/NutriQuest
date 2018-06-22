package com.uneatlantico.encuestas.Inicio

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.uneatlantico.encuestas.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [EndFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [EndFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class EndFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_end, container, false)
    }

    companion object {
        fun newInstance(): EndFragment = EndFragment()
    }
}
