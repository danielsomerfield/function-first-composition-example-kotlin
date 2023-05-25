package restaurantRatings.integration

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class Server {

    private lateinit var netty: NettyApplicationEngine

    fun start() {
        netty = embeddedServer(Netty, port = 8080, module = Application::server).start(wait = false)
    }

    fun stop() {
        netty.stop()
    }
}

fun Application.server() {
    routing {
        get("/{city}/restaurants/recommended") {
            call.respond(HttpStatusCode.OK)
        }
    }
}