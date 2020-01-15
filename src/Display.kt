package site.pegasis.hoot.server

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException

private val displays = ArrayList<DefaultWebSocketServerSession>()
private var displayState = DisplayState()

private val stateOverride: DisplayState? =
    null
//    DisplayState(
//    activity = DisplayActivity.COUNTDOWN,
//    countDownSeconds = 2,
//    questionIndex = 1
//)

suspend fun setDisplayState(action: DisplayState.() -> DisplayState) {
    displayState = action(displayState)
    val jsonObj = (stateOverride ?: displayState).toJSONObject()
    displays.forEach {
        it.send(jsonObj.toFrame())
    }
}

val displayHandler: suspend DefaultWebSocketServerSession.() -> Unit = {
    try {
        displays.add(this)
        send((stateOverride ?: displayState).toJSONObject().toFrame())
        while (true) {
            val frame = incoming.receive()
            val text = if (frame is Frame.Text) frame.readText() else continue
            try {
                val json = parseJSON(text)
                when (json["command"]) {
                    "start" -> startGame()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    } catch (e: ClosedReceiveChannelException) {
        println("onClose ${closeReason.await()}")
    } catch (e: Throwable) {
        println("onError ${closeReason.await()}")
        e.printStackTrace()
    } finally {
        displays.remove(this)
    }
}