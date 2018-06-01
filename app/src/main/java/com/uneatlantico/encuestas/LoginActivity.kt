package com.uneatlantico.encuestas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.firebase.iid.FirebaseInstanceIdService
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.actualizarUsuario
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.insertarUsuario
import java.util.*
import com.google.firebase.iid.FirebaseInstanceId
import com.uneatlantico.encuestas.NQController.Companion.guardarUsuario
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.guardarRespuesta


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 9001
    internal lateinit var mGoogleApiClient: GoogleApiClient
    internal lateinit var signInButton: SignInButton
    internal lateinit var signOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkUsuario())
            startNewActivity()
        else {
            setTheme(R.style.AppTheme)

            setContentView(R.layout.activity_login)

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .build()

            mGoogleApiClient = GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()

            signInButton = findViewById(R.id.sign_in_button2)
            signInButton.setSize(1)
            signInButton.setOnClickListener(this)

            signOutButton = findViewById(R.id.sign_in_button)
            signOutButton.setOnClickListener(this)
        }
    }

    private fun checkUsuario(): Boolean {
        var check: Boolean? = false
        try {
            val usuario = idUsuario(this)
            if (usuario != "")
                check = true
        } catch (e: Exception) {

        }

        return check!!

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button2 -> signIn()
            R.id.sign_in_button -> signOut()
        }
    }

    private fun mensaje(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle("Advertencia Debug")
        val dialog = builder.create()
        dialog.show()
    }


    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            handleSignInResult(result)

        }
    }

    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            insertUsuarioDB(result.signInAccount!!)
            startNewActivity()
        }
    }

    private fun insertUsuarioDB(acct: GoogleSignInAccount) {
        try {
            val idDispositivo = FirebaseInstanceId.getInstance().token
            Log.d("idUsuario", idDispositivo)
            val nombre = acct.displayName

            val email = acct.email
            val idPersona = email!!.split("@".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val photoUrl = acct.photoUrl.toString()
            val listaTemp = Arrays.asList<String>(idPersona[0], nombre, email, photoUrl, idDispositivo)

            guardarUsuario(this, listaTemp)

        } catch (e: Exception) {
            Log.d("noInsertadoLogin", e.message)
        }

    }

    private fun startNewActivity() {
        val i = Intent(this, NutriQuestMain::class.java)
        //i.putExtra("account", acct);
        i.putExtra("idPregunta", 0)
        //Log.d("jsonaccount" ,acct.toJson());
        //Kill the activity from which you will go to next activity
        startActivity(i)
        finish()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        mensaje("onConnectionFailed:$connectionResult")
    }

    private fun signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(object : ResultCallback<Status> {
            override fun onResult(status: Status) {
                mensaje("estas fuera")
            }
        })
    }

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    /*private lateinit var texto:TextView
    private lateinit var boton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        texto = findViewById(R.id.email)
        boton = findViewById(R.id.sign_in_button)
        boton.setOnClickListener{
            Log.d("textView", texto.text.toString())
            if(texto.text.toString() != "") {
                usuario()
                startNewActivity()
            }
            else
                Toast.makeText(this, "No puede estar vacio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun usuario() {
        Log.d("idUsuarioDb", "casi"+idUsuario(this))
        var insertar = false
        if(idUsuario(this) == "")
            insertar = true
        if(insertar)
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
    }*/

}
