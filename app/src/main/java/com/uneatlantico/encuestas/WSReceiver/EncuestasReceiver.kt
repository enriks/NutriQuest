package com.uneatlantico.encuestas.WSReceiver

import android.content.Context
import android.util.Log
import com.uneatlantico.encuestas.DB.NutriQuestExecuter.Companion.idUsuario
import com.uneatlantico.encuestas.DB.RespuestaRaw
import com.uneatlantico.encuestas.DB.RespuestasUsuario
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
        val url = URL("http://172.22.1.3/php/encuestasT/preguntaMaestra.php")
        //val url = URL("http://10.0.2.2/ws/encuestasWebService/preguntaMaestra.php")
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

fun sendUserResponses(a: ArrayList<RespuestaRaw>, idUsuario: String):String {
    var text = ""
    try {
        //val url = URL("http://10.0.2.2/ws/encuestasWebService/respuesta.php")
        val url = URL("http://172.22.1.3/php/encuestasT/respuesta.php")
        val conn = connectWS(url)

        val jsonA = JSONArray()

        a.forEach {
            //Log.d("respuestasJson", it.respuesta)
            val jsonParam = JSONObject()
            jsonParam.put("idRespuesta", it.idRespuesta)
            jsonParam.put("idUsuario", idUsuario)
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

        text = enviarWS(conn, json)
        Log.d("RespEnviarResp", text)
    }
    catch (e: Exception){
        Log.d("respsNoEnv", e.toString())
    }

    return text
}

/**
 * starts a conexion for the first time with web service via Username
 */
fun firstConexion(ct:Context, idEncuesta:Int):String {

    var text = ""
    try {
        //val url = URL("http://10.0.2.2/ws/encuestasWebService/primera_conexionOG.php")
        val url = URL("http://172.22.1.3/php/encuestasT/primera_conexionOG.php")
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

fun firstConexion2(ct:Context, idEncuesta:Int):String {

    var text = ""
    try {
        //val url = URL("http://10.0.2.2/ws/encuestasWebService/primera_conexion.php")
        val url = URL("http://172.22.1.3/php/encuestasT/primera_conexion.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("nombre", idUsuario(ct))
        jsonParam.put("idEncuesta", idEncuesta)

        Log.d("jsonPrimeraConn2", jsonParam.toString())

        text = enviarWS(conn, jsonParam)

        Log.d("textFirstConexion2", text)
    } catch (e: Exception) {
        Log.d("firstConexionExcp2", e.toString())
    }
    return text
}

fun enviarUsuario(usuario: List<String>):String {
    var text =""
    try {
        val url = URL("http://172.22.1.3/php/encuestasT/registrar.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("nombre", usuario[1])
        jsonParam.put("idPersona", usuario[0])
        jsonParam.put("email", usuario[2])
        jsonParam.put("idAndroid", usuario[4])

        Log.d("JsonEnviarUsr", jsonParam.toString())
        text = enviarWS(conn, jsonParam)
        Log.d("respuestaEnviarUsr", text)

    } catch (e: Exception) {
        Log.d("responseWebservice", e.toString())
    }
    return text
}

fun getPregunta(idEncuesta: Int, clave:String):String {
    var text = ""
    try {

        val url = URL("http://172.22.1.3/php/encuestasT/givePregunta.php")
        //val url = URL("http://10.0.2.2/ws/encuestasWebService/givePregunta.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("clave", clave)
        //jsonParam.put("idPregunta", idPregunta)
        jsonParam.put("idEncuesta", idEncuesta)


        Log.d("JsonGetPregunta", jsonParam.toString())
        text = enviarWS(conn, jsonParam)
        Log.d("respuestagetPr", text)

    } catch (e: Exception) {
        Log.d("getPreguntaExcp", e.toString())
    }
    return text
}

fun nQS(idPregunta: Int, idUsuario:String):String{
    var text = ""
    try {

        val url = URL("http://172.22.1.3/php/encuestasT/nQS.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("idPregunta", idPregunta)
        jsonParam.put("id", idUsuario)


        Log.d("JsonGetPregunta", jsonParam.toString())
        text = enviarWS(conn, jsonParam)
        Log.d("respuestagetPr", text)

    } catch (e: Exception) {
        Log.d("getPreguntaExcp", e.toString())
    }
    return text
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
    conn.readTimeout = 10000
    conn.connectTimeout = 10500
    conn.requestMethod = "POST"
    //conn.setRequestProperty("Content-Type", "false")
    conn.setRequestProperty("Accept", "application/json;charset=utf-8")
    conn.doInput = true
    conn.doOutput = true
    conn.connect()

    return conn
}
