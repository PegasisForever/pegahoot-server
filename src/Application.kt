package site.pegasis.hoot.server

import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.routing.routing
import io.ktor.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import java.time.Duration


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(60)
    }

    val users = HashMap<String, DefaultWebSocketServerSession>()
    val messageChannel = Channel<Map<String, String>>(Int.MAX_VALUE)

    routing {
        webSocket("/client") {
            var userName: String? = null
            try {
                while (true) {
                    val frame = incoming.receive()
                    val text = if (frame is Frame.Text) frame.readText() else continue

                    try {
                        val json = parseJSON(text)
                        when (json["command"]) {
                            "join" -> {
                                val name = json["name"] as String
                                val res = when {
                                    userName != null -> "joined"
                                    users.containsKey(name) -> "existed"
                                    else -> {
                                        users[name] = this
                                        userName = name
                                        messageChannel.send(mapOf("info" to "join", "name" to name))
                                        "success"
                                    }
                                }

                                send(mapOf("response" to res).toFrame())
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
                userName?.let {
                    users.remove(it)
                    messageChannel.send(mapOf("info" to "exit", "name" to userName))
                }
            }
        }

        webSocket("/display") {
            try {
                while (true) {
                    val msg=messageChannel.receive()
                    send(msg.toFrame())
                }
            } catch (e: ClosedReceiveChannelException) {
                println("onClose ${closeReason.await()}")
            } catch (e: Throwable) {
                println("onError ${closeReason.await()}")
                e.printStackTrace()
            }
        }
    }
}

