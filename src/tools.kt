package site.pegasis.hoot.server

import io.ktor.http.cio.websocket.Frame
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

private val jsonParser = JSONParser()
fun parseJSON(s: String): JSONObject {
    return jsonParser.parse(s) as JSONObject
}

fun String.toFrame(): Frame.Text {
    return Frame.Text(this)
}

fun JSONObject.toFrame(): Frame.Text {
    return toJSONString().toFrame()
}

fun Map<String, String>.toFrame(): Frame.Text {
    val obj = JSONObject()
    obj.putAll(this)
    return obj.toFrame()
}

fun Iterable<*>.toJSONArray() = JSONArray().apply {
    this.addAll(this@toJSONArray)
}