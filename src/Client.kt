package site.pegasis.hoot.server

import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.websocket.DefaultWebSocketServerSession
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import site.pegasis.hoot.server.parseJSON
import site.pegasis.hoot.server.toFrame

private val disconnectedStates = ArrayList<ClientState>()
val clients = HashMap<DefaultWebSocketServerSession, ClientState>()

suspend fun DefaultWebSocketServerSession.sendClientState(action: ClientState.() -> ClientState) {
    clients[this] = action(clients[this]!!)
    send(clients[this]!!.toJSONObject().toFrame())
}

fun DefaultWebSocketServerSession.getState() = clients[this]!!

val clientHandler: suspend DefaultWebSocketServerSession.() -> Unit = {
    clients[this] = ClientState()
    sendClientState { this }
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
                            getState().name != null -> sendClientState { copy(joinBtnErrorText = "You already joined.") }
                            clients.values.any { it.name == name } -> sendClientState {
                                copy(
                                    joinBtnErrorText = "This user name already existed."
                                )
                            }
                            else -> {
                                val disconnectedState = disconnectedStates.find { it.name == name }
                                sendClientState {
                                    if(disconnectedState!=null){
                                        disconnectedStates.remove(disconnectedState)
                                        disconnectedState
                                    }else{
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