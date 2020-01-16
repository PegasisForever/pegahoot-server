package site.pegasis.hoot.server

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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

fun List<*>.toJSONArray() = JSONArray().apply {
    val list = this@toJSONArray
    if (list.isEmpty()) {
        return@apply
    } else if (list.first() is JSONObjectAble) {
        addAll((list as List<JSONObjectAble>).map { it.toJSONObject() })
    } else {
        addAll(this@toJSONArray)
    }
}

interface JSONObjectAble {
    fun toJSONObject(): JSONObject
}

interface JSONArrayAble {
    fun toJSONArray(): JSONArray
}

fun WebSocketSession.sendAsync(frame: Frame) = GlobalScope.launch {
    send(frame)
}