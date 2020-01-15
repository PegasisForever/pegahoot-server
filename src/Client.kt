package site.pegasis.hoot.server

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException

private val disconnectedStates = ArrayList<ClientState>()
private val clients = HashMap<DefaultWebSocketServerSession, ClientState>()

val allClients: Map<DefaultWebSocketServerSession, ClientState>
    get() = clients
val namedClients: Map<DefaultWebSocketServerSession, ClientState>
    get() = clients.filter { it.value.name!="" }

private val stateOverride:ClientState?=
    null
//    ClientState(
//        activity = ClientActivity.GAME,
//        countDownSeconds = 2,
//        questionIndex = 1,
//        name = "Pega",
//        questionText = "Fill in the blanks",
//        questionSentence = "Pega __ xhx"
//)

suspend fun Map<DefaultWebSocketServerSession, ClientState>.setStates(action: ClientState.() -> ClientState) {
    clients.keys.forEach { session ->
        clients[session] = stateOverride?:action(clients[session]!!)
        session.send(clients[session]!!.toJSONObject().toFrame())
    }
}

suspend fun DefaultWebSocketServerSession.setClientState(action: ClientState.() -> ClientState) {
    clients[this] = stateOverride?:action(clients[this]!!)
    send(clients[this]!!.toJSONObject().toFrame())
}

fun DefaultWebSocketServerSession.getState() = clients[this]!!

val clientHandler: suspend DefaultWebSocketServerSession.() -> Unit = {
    clients[this] = ClientState()
    setClientState { this }
    try {
        while (true) {
            val frame = incoming.receive()
            val text = if (frame is Frame.Text) frame.readText() else continue

            try {
                val json = parseJSON(text)
                when (json["command"]) {
                    "join" -> {
                        val name = json["name"] as String
                        when {
                            getState().name != null -> setClientState { copy(joinBtnErrorText = "You already joined.") }
                            clients.values.any { it.name == name } -> setClientState {
                                copy(
                                    joinBtnErrorText = "This user name already existed."
                                )
                            }
                            else -> {
                                val disconnectedState = disconnectedStates.find { it.name == name }
                                setClientState {
                                    if (disconnectedState != null) {
                                        disconnectedStates.remove(disconnectedState)
                                        disconnectedState
                                    } else {
                                        copy(
                                            joinBtnErrorText = null,
                                            name = name,
                                            activity = ClientActivity.WAIT
                                        )
                                    }
                                }
                                setDisplayState {
                                    copy(
                                        users = clients.values.mapNotNull { it.name }
                                    )
                                }
                            }
                        }
                    }

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
        disconnectedStates.add(getState())
        clients.remove(this)
        setDisplayState {
            copy(
                users = clients.values.mapNotNull { it.name }
            )
        }
    }
}