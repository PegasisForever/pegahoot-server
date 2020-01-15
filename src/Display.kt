package site.pegasis.hoot.server

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlin.collections.ArrayList

private val displays = ArrayList<DefaultWebSocketServerSession>()
private var displayState = DisplayState()

val sortedUserScores:List<UserScore>
    get() = displayState.userScoreMap.toUserScoreList()

private val stateOverride: DisplayState? =
    null
//    DisplayState(
//        activity = DisplayActivity.GAME,
//        questionIndex = 1,
//        questionSentence = "question.sentence",
//        questionText = "question.question:",
//        questionLeftSeconds = 10,
//        answerTimes = listOf(AnswerTime("Pega", 3.78, true), AnswerTime("xhx", 5.55, false))
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