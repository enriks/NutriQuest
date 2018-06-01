package com.uneatlantico.encuestas

import android.content.Context
import android.util.Log
import com.uneatlantico.encuestas.NutriQuestExecuter.Companion.idUsuario
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun recibirPregunta(idPregunta: Int, ct: Context):Int {
    var enviado:Int = 0
    try {
        val url = URL("http://172.22.1.3/php/encuestas/pregunta.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("idPregunta", idPregunta)

        Log.d("JsonObject", jsonParam.toString())
        val text:String? = enviarWS(conn, jsonParam)

        if(text != null) {
            //Log.d("respuestaWS", text)

            val x = JSONArray(text)
            val jsonObject = x.getJSONObject(0)
            Log.d("respuestaWS", x.toString())

            enviado = 1
        }
    }
    catch (e: Exception){
        Log.d("responseWebservice", e.toString())
    }

    return enviado
}

fun sendUserResponses(a:ArrayList<RespuestasUsuario>,ct: Context):Int {
    var enviado:Int = 0
    try {
        val url = URL("http://172.22.1.3/php/encuestas/respuesta.php")
        val conn = connectWS(url)

        val jsonA = JSONArray()

        a.forEach {
            //Log.d("respuestasJson", it.respuesta)
            val jsonParam = JSONObject()
            jsonParam.put("respuesta", it.respuesta)
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
        Log.d("JsonObject", json.toString())

        val text: String? = enviarWS(conn, json)

        if(text != null) {
            Log.d("respuestaWS", text)

            //val x = JSONArray(text)
            val jsony = JSONObject(text)
            //val jsonObject = x.getJSONObject(0)
            Log.d("respuestaWS22", jsony.toString())

            enviado = 1
        }
    }
    catch (e: Exception){
        Log.d("responseWebservice", e.toString())
    }

    return enviado
}

/**
 * starts a conexion for the first time with web service via Username
 */
fun firstConexion(ct:Context) {
    try {
        val url = URL("http://172.22.1.3/php/encuestas/primera_conexion.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("nombre", idUsuario(ct))
        jsonParam.put("idEncuesta", 1)

        Log.d("JsonObject", jsonParam.toString())

        val text: String? = enviarWS(conn, jsonParam)


        if (text != null) {
            Log.d("respuestaWS", text)

            val jsony = JSONObject(text)
            Log.d("respuestaWS22", jsony.toString())
            Log.d("preguntarespuestaws", jsony["pregunta"].toString())
            Log.d("respuestarespuestaws", jsony["respuestas"].toString())
        }
    } catch (e: Exception) {
        Log.d("responseWebservice", e.toString())
    }
}

fun enviarUsuario(ct: Context, usuario: List<String>) {
    try {
        val url = URL("http://172.22.1.3/php/encuestas/registrar.php")
        val conn = connectWS(url)

        val jsonParam = JSONObject()
        jsonParam.put("nombre", usuario[1])
        jsonParam.put("idPersona", usuario[0])
        jsonParam.put("email", usuario[2])
        jsonParam.put("idAndroid", usuario[4])

        Log.d("JsonObject", jsonParam.toString())
        val text: String? = enviarWS(conn, jsonParam)

        if (text != null) {
            Log.d("enviarUsuario", text)

            //val x = JSONArray(text)
            val jsony = JSONObject(text)
            //val jsonObject = x.getJSONObject(0)
            Log.d("respuestaWS22", jsony.toString())

        }
    } catch (e: Exception) {
        Log.d("responseWebservice", e.toString())
    }
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
    conn.readTimeout = 1000
    conn.connectTimeout = 1500
    conn.requestMethod = "POST"
    //conn.setRequestProperty("Content-Type", "false")
    conn.setRequestProperty("Accept", "application/json;charset=utf-8")
    conn.doInput = true
    conn.doOutput = true
    conn.connect()

    return conn
}

//list.add(jsonObject.getString("valid"))
/*if(jsonObject.getString("valid") != null) {

    try {
        val horasArray1 = jsonObject.get("horasAlumno") as JSONArray
        horasAlumno = (horasArray1.getJSONObject(0).getDouble("horas")).toFloat()
    }
    catch (e:Exception){}
    try {
        if(postList[1].toInt() == 220 || postList[1].toInt() == 221)
            horasTotales = 20.0F
        else {
            horasTotales = 60.0F
            /*val horasArray2 = jsonObject.get("horasTotales") as JSONArray
            horasTotales = (horasArray2.getJSONObject(0).getDouble("horas")).toFloat()*/
        }
    }
    catch (e:Exception){}

    val progreso = Progreso(idEvento = postList[1].toInt(), horasAlumno = horasAlumno, horasEventoTotales = horasTotales)

    //Log.d("listaJson", horasAlumno= progreso.horasAlumno.toString() + " " +  progreso.horasEventoTotales.toString())

    insertarResponse(ct, progreso)
}*/
    //return enviado
