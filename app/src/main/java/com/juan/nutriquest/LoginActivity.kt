package com.juan.nutriquest

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.Intent
import android.os.PersistableBundle
import android.widget.Button
import android.widget.Toast
import com.juan.nutriquest.NutriQuestExecuter.Companion.actualizarUsuario
import com.juan.nutriquest.NutriQuestExecuter.Companion.idUsuario
import com.juan.nutriquest.NutriQuestExecuter.Companion.insertarUsuario

import kotlinx.android.synthetic.main.activity_login.*

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity() {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private lateinit var texto:TextView
    private lateinit var boton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        texto = findViewById(R.id.email)
        boton = findViewById(R.id.sign_in_button)
        boton.setOnClickListener{
            if(texto.text.toString() != "") {
                usuario()
                startNewActivity()
            }
            else
                Toast.makeText(this, "No puede estar vacio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun usuario() {
        if(idUsuario(this) != "")
            insertarUsuario(this, texto.text.toString())
        else
            actualizarUsuario(this, texto.text.toString())
    }

    private fun startNewActivity() {
        val i = Intent(this, NutriQuestMain::class.java)
        //i.putExtra("account", acct);

        //Log.d("jsonaccount" ,acct.toJson());
        //Kill the activity from which you will go to next activity
        startActivity(i)
        finish()
    }

}
