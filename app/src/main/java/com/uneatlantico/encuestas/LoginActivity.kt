package com.uneatlantico.encuestas

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
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
import java.util.*
import com.google.firebase.iid.FirebaseInstanceId
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.Inicio.NutriQuestMain
import com.uneatlantico.encuestas.Inicio.NQController.Companion.guardarUsuario
import org.jetbrains.anko.doAsync


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private val RC_SIGN_IN = 9001
    internal lateinit var mGoogleApiClient: GoogleApiClient
    internal lateinit var signInButton: SignInButton
//    internal lateinit var signOutButton: Button
    internal lateinit var texto:TextView
    internal lateinit var boton:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("hola", "hola")
        //Si ya hay un usuario logueado, pasar a la siguiente pantalla sin pasar por esta
        if (checkUsuario())
            startNewActivity()

        //inicializo esta pantalla
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

            signInButton = findViewById(R.id.sign_in_button_google)
            signInButton.setSize(1)
            signInButton.setOnClickListener(this)

            boton = findViewById(R.id.sign_in_button)
//            signOutButton.setOnClickListener(this)

            texto = findViewById(R.id.email)
            boton.setOnClickListener(this)
        }
    }

    /**
     * Compruebo si el usuario se logueo anteriormente Chequeando si existe algún registro el la tabla usuario
     */
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

    private fun signInNoGoogle(){

        Log.d("textView", texto.text.toString())
        if(texto.text.toString() != "") {
            usuarioLocal(texto.text.toString())
            startNewActivity()
        }
        else
            Toast.makeText(this, "No puede estar vacio", Toast.LENGTH_SHORT).show()

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_button_google -> signInGoogle()
            R.id.sign_in_button -> signInNoGoogle()
        }
    }

    private fun signInGoogle() {
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

    /**
     * tramito un login de google exitoso
     */
    private fun handleSignInResult(result: GoogleSignInResult) {
        if (result.isSuccess) {
            usuarioGoogle(result.signInAccount!!)
            startNewActivity()
        }
    }

    /**
     * Separo el usuario GOOGLE
     */
    private fun usuarioGoogle(acct: GoogleSignInAccount) {
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

    /**
     * Separo el usuario insertado en el textBox y relleno los campos para mandarlos a NQController que lo administre
     */
    private fun usuarioLocal(usuario:String):Int{
        var guardado = 0
        try {
            val idDispositivo = FirebaseInstanceId.getInstance().token
            Log.d("idUsuario", idDispositivo)
            val nombre = usuario

            val email = ""
            val idPersona = usuario
            val photoUrl = ""
            val listaTemp = Arrays.asList<String>(idPersona, nombre, email, photoUrl, idDispositivo)

            doAsync {guardarUsuario(applicationContext, listaTemp)}

        } catch (e: Exception) {
            Log.d("noInsertadoLogin", e.message)
        }
        return guardado
    }

    private fun startNewActivity() {
        val i = Intent(this, NutriQuestMain::class.java)
        //i.putExtra("account", acct);
        i.putExtra("idPregunta", 1)
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

    private fun mensaje(msg: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle("Advertencia Debug")
        val dialog = builder.create()
        dialog.show()
    }

}
