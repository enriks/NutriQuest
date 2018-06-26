package com.uneatlantico.encuestas.WSReceiver

import android.content.Context
import android.util.Log
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.DB.RespuestaRaw
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * json = {"pregunta":{"_id"...}, "respuestas":{1:{}, 2:{}, ...}, "categorias":{1:{}, 2:{},...}, "visibilidad":{}}
 */
fun recibirPregunta(idPregunta: Int, ct: Context):String {
    var text:String = ""
    try {
        //val url = URL("http://172.22.1.3/php/encuestas/preguntaMaestra.php")
        val url = URL("http://10.0.2.2/ws/encuestasWebService/preguntaMaestra.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("idPregunta", idPregunta)

        Log.d("JsonObject", jsonParam.toString())
        text = enviarWS(conn, jsonParam)

    }
    catch (e: Exception){
        Log.d("responseWebservice", e.toString())
    }

    return text
}

fun sendUserResponses(a: ArrayList<RespuestaRaw>, ct: Context):Int {
    var enviado:Int = 0
    try {
        val url = URL("http://10.0.2.2/ws/encuestasWebService/respuesta.php")
        //val url = URL("http://172.22.1.3/php/encuestas/respuesta.php")
        val conn = connectWS(url)

        val jsonA = JSONArray()

        a.forEach {
            //Log.d("respuestasJson", it.respuesta)
            val jsonParam = JSONObject()
            jsonParam.put("idRespuesta", it.idRespuesta)
            jsonParam.put("idUsuario", idUsuario(ct))
            jsonParam.put("idPregunta", it.idPregunta)
            jsonParam.put("idCategoria", it.idCategoria)
            jsonParam.put("idPreguntaPrevia", it.idPreguntaPrevia)
            jsonParam.put("idPreguntaPosterior", it.idPreguntaPosterior)
            jsonParam.put("contestado", it.contestado)
            jsonA.put(jsonParam)
        }

        val json = JSONObject()
        json.put("r", jsonA)
        Log.d("JsonEnviarResp", json.toString())

        val text: String? = enviarWS(conn, json)
        Log.d("RespEnviarResp", text)
    }
    catch (e: Exception){
        Log.d("respsNoEnv", e.toString())
    }

    return enviado
}

/**
 * starts a conexion for the first time with web service via Username
 */
fun firstConexion(ct:Context, idEncuesta:Int):String {

    var text = ""
    try {
        val url = URL("http://10.0.2.2/ws/encuestasWebService/primera_conexion.php")
        //val url = URL("http://172.22.1.3/php/encuestas/primera_conexion.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("nombre", idUsuario(ct))
        jsonParam.put("idEncuesta", idEncuesta)

        Log.d("jsonPrimeraConn", jsonParam.toString())

        text = enviarWS(conn, jsonParam)

    } catch (e: Exception) {
        Log.d("firstConexionExcp", e.toString())
    }
    return text
}

fun enviarUsuario(ct: Context, usuario: List<String>):Int {
    var enviado = 0
    try {
        val url = URL("http://172.22.1.3/php/encuestas/registrar.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("nombre", usuario[1])
        jsonParam.put("idPersona", usuario[0])
        jsonParam.put("email", usuario[2])
        jsonParam.put("idAndroid", usuario[4])

        Log.d("JsonEnviarUsr", jsonParam.toString())
        val text: String? = enviarWS(conn, jsonParam)
        Log.d("respuestaEnviarUsr", text)

    } catch (e: Exception) {
        Log.d("responseWebservice", e.toString())
    }
    return enviado
}

fun enviarWS(conn: HttpURLConnection, jsonParam: JSONObject):String{
    val outputStream = conn.outputStream
    val outputStreamWriter = OutputStreamWriter(outputStream, "UTF-8")
    outputStreamWriter.write(jsonParam.toString())
    outputStreamWriter.flush()
    outputStreamWriter.close()
    return conn.inputStream.use { it.reader().use { reader -> reader.readText() } }
}

fun connectWS(url:URL):HttpURLConnection{
    val conn = url.openConnection() as HttpURLConnection
    conn.readTimeout = 2000
    conn.connectTimeout = 2500
    conn.requestMethod = "POST"
    //conn.setRequestProperty("Content-Type", "false")
    conn.setRequestProperty("Accept", "application/json;charset=utf-8")
    conn.doInput = true
    conn.doOutput = true
    conn.connect()

    return conn
}
