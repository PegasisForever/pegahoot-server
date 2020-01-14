package site.pegasis.hoot.server

import ClientActivity
import ClientState
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(60)
    }

    val disconnectedStates = ArrayList<ClientState>()
    val clients = HashMap<DefaultWebSocketServerSession, ClientState>()
    val displays = ArrayList<DefaultWebSocketServerSession>()
    var displayState = DisplayState()

    suspend fun sendDisplayState(action: DisplayState.() -> DisplayState) {
        displayState = action(displayState)
        val jsonObj = displayState.toJSONObject()
        displays.forEach {
            it.send(jsonObj.toFrame())
        }
    }

    suspend fun DefaultWebSocketServerSession.sendClientState(action: ClientState.() -> ClientState) {
        clients[this] = action(clients[this]!!)
        send(clients[this]!!.toJSONObject().toFrame())
    }

    fun DefaultWebSocketServerSession.getState() = clients[this]!!

    routing {
        webSocket("/client") {
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
                                        sendDisplayState {
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
                sendDisplayState {
                    copy(
                        users = clients.values.mapNotNull { it.name }
                    )
                }
            }
        }

        webSocket("/display") {
            try {
                displays.add(this)
                send(displayState.toJSONObject().toFrame())
                while (true) {
                    val frame = incoming.receive()
                    val text = if (frame is Frame.Text) frame.readText() else continue
                    println(text)
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
    }

    GlobalScope.launch {

    }
}


